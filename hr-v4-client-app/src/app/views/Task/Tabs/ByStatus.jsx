import { observer } from "mobx-react";
import React, { useEffect, useState } from "react";
import { Grid } from "@material-ui/core";
import Board, { moveCard } from "@asseinfo/react-kanban";
import "@asseinfo/react-kanban/dist/styles.css";
import moment from "moment";
import TaskForm from "../TaskForm";
import AddIcon from "@material-ui/icons/Add";
import { useStore } from "../../../stores";
import { updateStatus } from "../TaskService";
import DescriptionIcon from "@material-ui/icons/Description";

export default observer(function AllTask() {

    const { taskStore } = useStore();
    const { listTask: board, getAllTask, handleOpenPopupForm } = taskStore;

    useEffect(() => {
        getAllTask(2);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const ControlledBoard = () => {
        const [controlledBoard, setBoard] = useState(board);

        function onCardMove(board, card, source, destination) {

            updateStatus({
                taskId: card.id,
                workingStatusId: destination.toColumnId,
            });
            const updatedBoard = moveCard(controlledBoard, source, destination);

            setBoard(updatedBoard);
        }

        return (
            <Board
                onCardDragEnd={onCardMove}
                initialBoard={board}
                renderCard={(values) => {
                    return (
                        <div
                            className="kanban-card"
                            onClick={() => handleOpenPopupForm(values.id)}
                        >
                            <div style={{ display: "flex", marginBottom: "5px" }}>
                                <DescriptionIcon /> <h5 style={{ marginBottom: "5px", marginLeft: "3px", maxWidth: "150px", overflow: "hidden", textOverflow: "ellipsis" }} >{values?.name || ""}</h5>
                            </div>
                            <div>
                                <p className="card-project">{values.project?.name}</p>
                            </div>
                            {values?.priority && (
                                <div>
                                    <p className="card-priority"> {
                                        values.priority === 1 ? <span style={{ backgroundColor: "rgb(10 255 15)", padding: "2px 4px", borderRadius: "3px" }}>Thấp</span>
                                            : values.priority === 2 ? <span className="tag bgc-primary">Trung bình</span>
                                                : values.priority === 3 ? <span className="tag bgc-warning-d1">Cao</span>
                                                    : values.priority === 4 ? <span className="tag bgc-danger-tp1">Cấp bách</span> : <></>}
                                    </p>
                                </div>
                            )}
                            <p>
                                {(values.staffs && values.staffs.length > 0) ? values?.staffs.map((item) => (
                                    <>{item.displayName}, </>
                                )) : (
                                    <>Chưa có nhân viên nào</>
                                )}
                            </p>
                            <p>{values.startTime ? moment(values.startTime).format("hh:mm DD-MM-YYYY") : null}</p>
                        </div>
                    );
                }}
                renderColumnHeader={(values) => {
                    return (
                        <p className="kanban-column-header">
                            <div style={{ display: "flex", alignItems: "center" }}>
                                {<span className="tag bgc-primary" style={{ float: "right" }}>{values?.title}</span>}
                                <p>
                                    {values?.cards?.length}
                                </p>
                            </div>
                            <button
                                type="button"
                                onClick={() => handleOpenPopupForm(null, values.id)}
                                className="button"
                            >
                                <AddIcon />
                                New
                            </button>
                        </p>
                    );
                }}
            >
                {controlledBoard}
            </Board>
        );
    };
    return (
        <>
            <Grid className="index-card" >
                <Grid item xs={12} className="kanban-container">
                    <ControlledBoard />
                </Grid>
            </Grid>
            <TaskForm />
        </>
    );
});
