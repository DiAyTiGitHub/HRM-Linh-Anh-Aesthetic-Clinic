import React, { memo, useState } from 'react'
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from 'mobx-react';

import { makeStyles } from "@material-ui/core/styles";
import Timeline from "@material-ui/lab/Timeline";
import TimelineItem from "@material-ui/lab/TimelineItem";
import TimelineSeparator from "@material-ui/lab/TimelineSeparator";
import TimelineConnector from "@material-ui/lab/TimelineConnector";
import TimelineContent from "@material-ui/lab/TimelineContent";
import TimelineOppositeContent from "@material-ui/lab/TimelineOppositeContent";
import Paper from "@material-ui/core/Paper";
import Typography from "@material-ui/core/Typography";
import GlobitsAvatar from 'app/common/GlobitsAvatar';
import Tooltip from '@material-ui/core/Tooltip';
import FiberManualRecordIcon from '@material-ui/icons/FiberManualRecordOutlined';
import GlobitsEditor from 'app/common/form/GlobitsEditor';
import { Form, Formik } from 'formik';

const useStyles = makeStyles((theme) => ({
    paper: {
        backgroundColor: '#e9e9f1',
        padding: "8px",
        borderRadius: "0px",
        borderTopLeftRadius: "8px",
        borderTopRightRadius: "8px",
        // boxShadow: "rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px",
        // boxShadow: "rgb(204, 219, 232) 3px 3px 6px 0px inset, rgba(255, 255, 255, 0.5) -3px -3px 6px 1px inset",
        // boxShadow: "rgba(0, 0, 0, 0.25) 0px 0.0625em 0.0625em, rgba(0, 0, 0, 0.25) 0px 0.125em 0.5em, rgba(255, 255, 255, 0.1) 0px 0px 0px 1px inset",
        // boxShadow: "-6px -6px 9px 0px rgba(237, 237, 237, 1)",
        boxShadow: "rgba(67, 71, 85, 0.27) 0px 0px 0.25em, rgba(90, 125, 188, 0.05) 0px 0.25em 1em"
    },
    timelineStyle: {
        marginBottom: "20px",
    },
    secondaryTail: {
        backgroundColor: theme.palette.secondary.main,
    },
    userImage: {
        width: "44px",
        height: "44px",
        borderRadius: "50px",
        maxWidth: "unset !important",
        marginBottom: "4px",
        boxShadow: "rgba(0, 0, 0, 0.15) 0px 2px 8px"
    },
    dateTimeText: {
        textShadow: "0px 1px 4px #c0c0c0",
    },
    listItem: {
        listStyleType: "none",
        padding: 0,
        // textShadow: "0 1px 1px #c4b59d,0 1px 1px black"
    },
    displayUsername: {
        color: "#1b5c5c !important;"
    },
}));

function HistoryItem(props) {
    const { history, isEnd } = props;

    const classes = useStyles();

    const { taskHistoryStore, taskStore } = useStore();

    const {
        timestampToDate,
        convertToYMDAgo,
        getKeyTitle,
        getValueTitleType,
        isObject,
        isContentEmpty
    } = taskHistoryStore;

    const { getPriorityTitleAndColor } = taskStore;

    const displayUsername = `${history?.modifier?.displayName} (${history?.modifier?.username})`;

    let description = null;
    let comment = null;
    if (history?.event) {
        Object.keys(history?.event).forEach((key, index) => {
            const value = history?.event[key];

            if (key == "description") description = value;
            else if (key == "comment") comment = value;
        })
    }

    return (
        <TimelineItem className={classes?.timelineStyle}>
            <TimelineOppositeContent className="display-none"></TimelineOppositeContent>

            <TimelineSeparator className="">
                <Tooltip title={`${history?.modifier?.displayName} (${history?.modifier?.username})`} placement="right">
                    <GlobitsAvatar imgPath={history?.modifier?.imagePath} name={history?.modifier?.displayName} className={classes?.userImage} />
                </Tooltip>

                {!isEnd && (
                    <TimelineConnector />
                )}
            </TimelineSeparator>

            <TimelineContent className="pt-0 pl-12">
                <Paper elevation={3} className={classes.paper}>
                    <div className='flex justify-left'>
                        {history?.event?.isNew ?
                            (
                                <h6 className='m-0'>
                                    <b>Công việc được tạo bởi <span className={classes.displayUsername}>
                                        {displayUsername}
                                    </span>
                                    </b>
                                </h6>
                            )
                            :
                            (
                                <h6 className='m-0'>
                                    <b>Công việc được cập nhật bởi <span className={classes.displayUsername}>
                                        {displayUsername}
                                    </span>
                                    </b>
                                </h6>
                            )
                        } {(
                            <h6 className="mb-0 ml-4 text-primary-d2">
                                [{
                                    <span className={classes?.dateTimeText}>
                                        {timestampToDate(history?.date)}
                                    </span>
                                }] ({(convertToYMDAgo(history?.date))})
                            </h6>
                        )}

                    </div>
                </Paper>

                {
                    history?.event && (
                        <ol className='pl-0 pb-0'>
                            {
                                history?.event && Object.keys(history?.event).map((key, index) => {
                                    const value = history?.event[key];

                                    function getDisplayElement() {
                                        if (key == "countSubtaskUpdated") {
                                            return (<span className='historyLogItem'>
                                                <b><span className="text-green">{value}</span> {" hạng mục công việc con"} {history?.event?.countSubTaskUnchecked && " khác"}</b> đã được đánh dấu hoàn thành
                                            </span>);
                                        }

                                        if (key == "countSubTaskUnchecked") {
                                            return (<span className='historyLogItem'>
                                                <b><span className="text-red">{value}</span> {" hạng mục công việc con"}</b> đã bị bỏ đánh dấu hoàn thành
                                            </span>);
                                        }

                                        const keyTitle = getKeyTitle(key);
                                        if (key == "description") {
                                            return (
                                                <span className='historyLogItem'>
                                                    <b>{keyTitle}</b> đã được cập nhật
                                                </span>
                                            );
                                        }

                                        if (key == "comment") {
                                            return (
                                                <></>
                                            );
                                        }

                                        const valueTitleType = getValueTitleType(value);
                                        //handle for case the old data in database
                                        if (valueTitleType == 0) {
                                            return (
                                                <span className='historyLogItem'>
                                                    {value}
                                                </span>
                                            );
                                        }

                                        //handle for case the new value is recorded
                                        if (valueTitleType == 1) {
                                            return (
                                                <span className='historyLogItem'>
                                                    <b>{keyTitle}</b> đã được tạo mới (<span className='text-italic'>
                                                        {key == "priority" ? (
                                                            <span style={{ fontWeight: "bold", color: getPriorityTitleAndColor(value?.newValue)?.color }}>
                                                                {getPriorityTitleAndColor(value?.newValue)?.title}
                                                            </span>
                                                        ) : (
                                                            <>
                                                                {value?.newValue}
                                                            </>
                                                        )}
                                                    </span>)
                                                </span>
                                            );
                                        }

                                        //handle for case the old value is delete
                                        if (valueTitleType == 2) {
                                            return (
                                                <span className='historyLogItem'>
                                                    <b>{keyTitle}</b> cũ đã bị xóa (<span className='text-italic'>
                                                        {key == "priority" ? (
                                                            <span style={{ fontWeight: "bold", color: getPriorityTitleAndColor(value?.oldValue)?.color }}>
                                                                {getPriorityTitleAndColor(value?.oldValue)?.title}
                                                            </span>
                                                        ) : (
                                                            <>
                                                                {value?.oldValue}
                                                            </>
                                                        )}
                                                    </span>)
                                                </span>
                                            );
                                        }

                                        //handle for case the value is changing
                                        if (valueTitleType == 3) {
                                            return (
                                                <span className='historyLogItem'>
                                                    <b>{keyTitle}</b> thay đổi từ
                                                    <span className='text-italic'>
                                                        {key == "priority" ? (
                                                            <span style={{ fontWeight: "bold", color: getPriorityTitleAndColor(value?.oldValue)?.color }}>
                                                                {" " + getPriorityTitleAndColor(value?.oldValue)?.title + " "}
                                                            </span>
                                                        ) : (
                                                            <>
                                                                {" " + value?.oldValue + "  "}
                                                            </>
                                                        )}
                                                    </span> thành <span className='text-italic'>
                                                        {key == "priority" ? (
                                                            <span style={{ fontWeight: "bold", color: getPriorityTitleAndColor(value?.newValue)?.color }}>
                                                                {" " + getPriorityTitleAndColor(value?.newValue)?.title + " "}
                                                            </span>
                                                        ) : (
                                                            <>
                                                                {" " + value?.newValue + "  "}
                                                            </>
                                                        )}
                                                    </span>
                                                </span>
                                            );
                                        }
                                    }


                                    return (
                                        <>
                                            {(value === true || value == "true" || key == "comment") ? <></> : (
                                                <li key={index} className={`${classes.listItem} mt-2 flex justufy-left align-center`}>
                                                    <FiberManualRecordIcon style={{ width: "8px", height: "8px", opacity: 0.6 }} className='mr-4' />

                                                    <p className={`m-0`}>
                                                        {getDisplayElement()}
                                                    </p>
                                                </li>
                                            )}
                                        </>
                                    );
                                })
                            }

                            {
                                comment && !isContentEmpty(comment) && (
                                    <div className="notePaperHistoryLog">
                                        {/* <Formik
                                            enableReinitialize
                                            initialValues={{ comment }}
                                        >
                                            {({ values }) => (
                                                // <Form>
                                                //     <GlobitsEditor
                                                //         readOnly
                                                //         name="comment"
                                                //     />
                                                // </Form>
                                                <div dangerouslySetInnerHTML={{ __html: comment }}></div>
                                            )}
                                        </Formik> */}


                                        <div dangerouslySetInnerHTML={{ __html: comment }}></div>
                                    </div>
                                )
                            }
                        </ol>
                    )
                }

            </TimelineContent>
        </TimelineItem>
    );
}

export default memo(observer(HistoryItem));