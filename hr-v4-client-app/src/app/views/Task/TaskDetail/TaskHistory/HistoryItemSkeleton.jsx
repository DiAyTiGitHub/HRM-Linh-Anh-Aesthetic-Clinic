import React, { memo, useState } from 'react'
import { observer } from 'mobx-react';

import { makeStyles } from "@material-ui/core/styles";
import TimelineItem from "@material-ui/lab/TimelineItem";
import TimelineSeparator from "@material-ui/lab/TimelineSeparator";
import TimelineContent from "@material-ui/lab/TimelineContent";
import TimelineOppositeContent from "@material-ui/lab/TimelineOppositeContent";
import Typography from "@material-ui/core/Typography";

import Skeleton from '@material-ui/lab/Skeleton';

const useStyles = makeStyles((theme) => ({
    paper: {
        padding: "8px",
        borderRadius: "6px",
        boxShadow: "rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 2px 6px 2px",
        boxShadow: "rgba(50, 50, 93, 0.25) 0px 6px 12px -2px, rgba(0, 0, 0, 0.3) 0px 3px 7px -3px"

    },
    timelineStyle: {
        paddingBottom: "8px"
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
        color: "black",
        textShadow: "0px 1px 4px #c0c0c0"
    },
    listItem: {
        listStyleType: "none",
        padding: 0,
    },

}));

function HistoryItemSkeleton() {

    const classes = useStyles();
 
    return (
        <TimelineItem className={classes?.timelineStyle}>
            <TimelineOppositeContent className="display-none"></TimelineOppositeContent>

            <TimelineSeparator className="pt-6">
                <Skeleton animation="wave" variant="circle" width={44} height={44} />
            </TimelineSeparator>

            <TimelineContent className="pt-0 pl-12">
                <Typography component="div" variant="body2">
                    <Skeleton />
                </Typography>

                <Typography component="div" variant={"h3"}>
                    <Skeleton />
                </Typography>

            </TimelineContent>
        </TimelineItem>
    );
}

export default memo(observer(HistoryItemSkeleton));