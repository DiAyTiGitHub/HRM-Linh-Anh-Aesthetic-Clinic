import React, { memo, useState } from 'react'
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { observer } from 'mobx-react';
import { useEffect } from 'react';
import { Grid, List, ListItem } from '@material-ui/core';
import { useHistory } from "react-router-dom";
import Timeline from "@material-ui/lab/Timeline";
import HistoryItemSkeleton from './HistoryItemSkeleton';
import HistoryItem from './HistoryItem';
import TimelineIcon from '@material-ui/icons/Timeline';
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";



function IntegratedTaskHistory() {
    const { t } = useTranslation();
    const history = useHistory();

    const { taskStore, taskHistoryStore } = useStore();

    const {
        dataTaskForm: chosenTask
    } = taskStore;

    const [pageNumber, setPageNumber] = useState(1);

    const {
        pagingHistoryOfTask,
        loadedHistory,
        canLoadMore,
        resetStore
    } = taskHistoryStore;

    const [isLoading, setLoading] = useState(true);

    useEffect(function () {
        async function loadHistory() {
            setLoading(true);

            const searchObj = {
                taskId: chosenTask?.id,
                pageIndex: pageNumber
            };

            if (chosenTask && chosenTask?.id)
                await pagingHistoryOfTask(searchObj);

            setLoading(false);

        }

        loadHistory();

    }, [pageNumber]);

    function handleLoadMoreHistory() {
        setPageNumber(pageNumber + 1);

    }

    useEffect(function () {
        setPageNumber(1);
        resetStore();

    }, [chosenTask?.id]);

    useEffect(function () {
        //cleanup function
        return (resetStore);
    }, []);

    return (
        <>
            {
                (loadedHistory && loadedHistory?.length != 0) && (
                    <>
                        <Grid item xs={12}>
                            <div className="flex justify-left pt-8">
                                <TimelineIcon className="mr-8" /> Cập nhật gần đây:
                            </div>
                        </Grid>

                        <Grid item xs={12} className="pt-0">
                            <div className="integratedTaskHistory">
                                <List className="timeLineContainer">
                                    <Timeline align="left" id="timeLineElement">
                                        {
                                            loadedHistory && (
                                                <>
                                                    {
                                                        loadedHistory?.map(function (history, index) {
                                                            const isEnd = index === loadedHistory?.length - 1;
                                                            return (
                                                                <HistoryItem
                                                                    history={history}
                                                                    key={index}
                                                                    isEnd={isEnd}
                                                                />
                                                            );
                                                        })
                                                    }

                                                    {
                                                        canLoadMore ? (
                                                            <button
                                                                type="button"
                                                                onClick={handleLoadMoreHistory}
                                                                className="w-100 loadMoreButton"
                                                            >
                                                                <MoreHorizIcon className="mr-8" />
                                                                Tải thêm lịch sử
                                                            </button>
                                                        ) : (
                                                            // <p className="w-100 m-0 text-center endHistoryNote">
                                                            //     -------- Tất cả cập nhật đã được hiển thị ---------
                                                            // </p>
                                                            <></>
                                                        )
                                                    }
                                                </>
                                            )

                                        }

                                        {
                                            !isLoading && (!loadedHistory || loadedHistory?.length == 0) && (
                                                <ListItem>
                                                    <span className='w-100 flex flex-center'>
                                                        <b>
                                                            Nhiệm vụ này chưa có lịch sử nào được ghi
                                                        </b>
                                                    </span>
                                                </ListItem>
                                            )
                                        }

                                        {
                                            isLoading && (
                                                <>
                                                    <HistoryItemSkeleton />
                                                    <HistoryItemSkeleton />
                                                    <HistoryItemSkeleton />
                                                </>
                                            )
                                        }



                                    </Timeline>
                                </List>

                            </div>

                        </Grid>
                    </>
                )
            }
        </>
    );
}

export default memo(observer(IntegratedTaskHistory));




