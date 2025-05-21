import { observer } from "mobx-react";
import React, { memo } from "react";
import { Grid, Tooltip } from "@material-ui/core";
import Board, { moveCard } from "@asseinfo/react-kanban";
import "@asseinfo/react-kanban/dist/styles.css";
import moment from "moment";
import AddIcon from "@material-ui/icons/Add";
import { useStore } from "../../../../stores";
import { updateStatus } from "../../TaskService";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import EditIcon from '@material-ui/icons/Edit';
import { makeStyles } from "@material-ui/core";



function TaskKanban() {
    const { taskStore } = useStore();
    let {
        handleOpenTaskPopup,
        getMoreTaskByStatus,
        getListByLimitTask,
        listTaskKanban,
        handleChangeListTaskKanban,
        currentProject,
        getPriorityTitleAndColor,
        setNeedReloadKanbanHeader,
        autoLoadMoreKanban
    } = taskStore;

    const eachColumnWidth = 100 / listTaskKanban?.length + "% !important";
    const useStyles = makeStyles((theme) => ({
        kanbanContainer: {
            '& .react-kanban-column': {
                width: eachColumnWidth
            }
        }
    }));
    const classes = useStyles();


    const ControlledBoard = () => {

        async function onCardMove(_, card, source, destination) {
            // console.log("changing column: ", card);
            // console.log("source:", source);
            // console.log("destination:", destination); 

            const { data: updatedCard } = await updateStatus({ taskId: card?.id, workingStatusId: destination?.toColumnId });

            // //IMMEDIATELY UPDATE TASK KANBAN
            // //this is list kanban column
            // const newListTaskKanban = listTaskKanban;

            // //find column to update by its index
            // const columnIndex = newListTaskKanban?.findIndex(e => e?.id === values?.status?.id);

            // const indexCardsOld = newListTaskKanban.findIndex(e => e?.id === this?.dataTaskForm?.status?.id);
            // newListTaskKanban[indexCardsOld].cards = newListTaskKanban[indexCardsOld]?.cards?.filter(e => e?.id !== data?.id);
            // newListTaskKanban[columnIndex].cards = [updatedCard, ...newListTaskKanban[columnIndex]?.cards];


            const updatedBoard = moveCard({ columns: listTaskKanban }, source, destination);
            handleChangeListTaskKanban(updatedBoard?.columns);

            //reload number of task in each column in kanban header
            setNeedReloadKanbanHeader(true);

            //load 1 more task if number of cards remaining in column is less than 10
            autoLoadMoreKanban(source?.fromColumnId);
        }

        return (
            <Board
                onCardDragEnd={onCardMove}
                addNewButton
                addNewButtonDisplayMode='bottom'
                disableColumnDrag
                initialBoard={{ columns: listTaskKanban }}
                renderCard={(values) => {
                    const cardProps = getPriorityTitleAndColor(values?.priority);
                    const kanbanCardStyle = {
                        borderLeftColor: cardProps?.color
                    };

                    const hasCode = values?.code;

                    return (
                        <div
                            key={values?.id}
                            className="kanban-card"
                            style={kanbanCardStyle}
                        >

                            <div className="flex align-start justify-between">
                                {hasCode && (
                                    <p className="cardCode">{values?.projectCode}{values?.code && ("#" + values?.code)}</p>
                                )}

                                {!hasCode && (
                                    <h5
                                        className="card-name flex-grow-1"
                                    >
                                        {values?.name || ""}
                                    </h5>
                                )}

                                <Tooltip title="Cập nhật" placement="top">
                                    {/* <MoreVertIcon
                                        className="moreIcon"
                                        onClick={() => handleOpenTaskPopup(values?.id)}
                                    /> */}
                                    <EditIcon
                                        className="moreIcon"
                                        onClick={() => handleOpenTaskPopup(values?.id)}
                                    />

                                </Tooltip>

                            </div>

                            {hasCode && (
                                <h5
                                    className="card-name flex-grow-1"
                                >
                                    {values?.name || ""}
                                </h5>
                            )}

                            {values?.projectName && (
                                <p className="cardAttribute">
                                    <b>Dự án:</b> {" " + values?.projectName}
                                </p>
                            )}

                            {values?.activity && (
                                <p className="cardAttribute">
                                    <b>Hoạt động:</b> {" " + values?.activity}
                                </p>
                            )}

                            {values?.priority && (
                                <p className="cardAttribute">
                                    <b>Độ ưu tiên:</b> {" " + cardProps?.title}
                                </p>
                            )}


                            {(values?.assignee) && (
                                <p className="cardAttribute">
                                    <b>Phụ trách:</b>
                                    {
                                        " " + values?.assignee
                                    }
                                </p>
                            )}

                            {values?.startTime && (
                                <p className="cardAttribute"><b>Ngày bắt đầu:</b> {" " + moment(values?.startTime).format("hh:mm DD-MM-YYYY")}</p>
                            )}

                            {values?.lastModifyDate && (
                                <p className="cardAttribute"><b>Cập nhật cuối:</b> {" " + moment(values?.lastModifyDate).format("hh:mm DD-MM-YYYY")}</p>
                            )}
                        </div>
                    );
                }}
                renderColumnHeader={(column) => {
                    return (
                        <React.Fragment key={column?.id}>
                            <div className="kanban-column-header bgc-lighter-dark-blue">
                                <span className="tag">{column?.name}</span>

                                <div className="flex align-center justify-center headerInfoContainer">
                                    <Tooltip title="Số lượng việc đang ở trạng thái này" placement="top">
                                        <span
                                            className="cursor-pointer"
                                        >
                                            {/* show total number of tasks in this status */}
                                            ({column?.totalOfTasksInStatus})
                                        </span>
                                    </Tooltip>

                                    <Tooltip title="Công việc mới" placement="top">
                                        <button
                                            type="button"
                                            onClick={() => handleOpenTaskPopup(null, { ...column, cards: null })}
                                            className="button m-0 p-0 ml-4"
                                        >
                                            <AddIcon className="" />
                                        </button>
                                    </Tooltip>
                                </div>
                            </div>



                            {!column?.last && (
                                <Tooltip title="Tải thêm dữ liệu" placement="bottom">
                                    <button
                                        type="button"
                                        onClick={() => getMoreTaskByStatus(column?.id)}
                                        className="w-100 loadMoreCardBtn"
                                    >
                                        <MoreHorizIcon className="mr-2" />
                                        More
                                    </button>
                                </Tooltip>
                            )}


                        </React.Fragment>

                    );
                }}
            />
        );
    };

    return (
        <Grid className="index-card" >
            <Grid item xs={12} className={`kanban-container ${classes.kanbanContainer}`}>
                <ControlledBoard />
            </Grid>
        </Grid>
    );
}

export default memo(observer(TaskKanban));
