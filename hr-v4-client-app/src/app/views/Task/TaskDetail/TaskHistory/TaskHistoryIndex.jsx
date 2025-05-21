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



function TaskHistory() {
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
                taskId: chosenTask.id,
                pageIndex: pageNumber
            };

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
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <List className="timeLineContainer">
                    <Timeline align="left" id="timeLineElement">

                        {
                            !isLoading && loadedHistory && (
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
                                        canLoadMore && (
                                            <p
                                                className="loadMoreButton"
                                                onClick={handleLoadMoreHistory}
                                            >
                                                <b>
                                                    -------- Tải thêm lịch sử ---------
                                                </b>
                                            </p>
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
            </Grid>
        </Grid>
    );
}

export default memo(observer(TaskHistory));




