import React from 'react'
import { Grid, Collapse, Button } from "@material-ui/core";
import { useStore } from '../../../../stores';
import { observer } from 'mobx-react';
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import ArrowRightIcon from "@material-ui/icons/ArrowRight";
import DescriptionIcon from "@material-ui/icons/Description";
import TaskForm from '../../TaskForm';
import { useState } from 'react';
import LocalConstants from 'app/LocalConstants';

export default observer(function TaskGroup() {

    const { taskStore } = useStore();

    const { handleOpenPopupForm, listAllTask } = taskStore;

    const [visible, setVisible] = React.useState(-1);
    const [listTask, setListTask] = useState([]);

    React.useEffect(() => {
        if (listAllTask.length > 0) {
            setListTask(LocalConstants.Priority.map((item) => ({
                id: item.id,
                name: item.name,
                className: item.className,
                listItem: listAllTask.filter((values) => values?.priority === item.id),
            })))
        }
    }, [listAllTask]);

    return (
        <div className="content-index">
            <Grid className="index-card" style={{ marginTop: 10 }} >
                <Grid item xs={12} className="kanban-container">
                    {listTask.length > 0 && listTask.map((priority, index) => (
                        <div style={{ display: "flex" }}>
                            <Button onClick={() => { setVisible(visible === index ? -1 : index) }} style={{ margin: "10px", maxHeight: "30px" }} >
                                {visible === index ? <ArrowDropDownIcon /> : <ArrowRightIcon />}
                            </Button>
                            <div key={priority.id} style={{ borderBottom: "1.5px solid #00000036", display: "unset" }}>
                                <p>
                                    <span className={`tag ${priority.className}`} style={{ backgroundColor: "rgb(10 255 15)" }} >{priority.name}</span> {priority.listItem.length}
                                </p>
                                <Collapse in={visible === index}>
                                    {priority.listItem.length > 0 ? (
                                        <>{
                                            priority.listItem.map((item) => (
                                                <div key={item.id} style={{ display: "flex", margin: "10px 0px" }} onClick={() => handleOpenPopupForm(item.id)}><DescriptionIcon />
                                                    <div style={{ fontSize: "18px", textDecoration: "underline rgba(170, 170, 170, 0.42)", width: "100%" }} >{item?.name}</div>
                                                    <div >
                                                        {item?.status?.name === "Testing" ? <span className="tag bgc-orange" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                            : item?.status?.name === "On Hold" ? <span className="tag bgc-brown" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                                : item?.status?.name === "Dev Done" ? <span className="tag bgc-success" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                                    : item?.status?.name === "New" ? <span className="tag bgc-primary" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                                        : item?.status?.name === "Test Done" ? <span className="tag bgc-warning-d1" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                                            : item?.status?.name === "Coding" ? <span className="tag bgc-yellow" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span>
                                                                                : item?.status?.name === "Completed" ? <span className="tag bgc-danger-tp1" style={{ float: "right", width: "100%" }}>{item?.status?.name}</span> : <></>}
                                                    </div>
                                                </div>
                                            ))
                                        }</>
                                    ) : (
                                        <div>Chưa có task nào</div>
                                    )}
                                </Collapse>
                            </div>
                        </div>
                    ))}
                    
                    <TaskForm />
                </Grid>
            </Grid>
        </div>
    )
})