import React, { memo } from "react";
import "react-toastify/dist/ReactToastify.css";

// pop up
import { observer } from "mobx-react";
import { Skeleton } from "@material-ui/lab";


function LoadingTabSkeleton() {

    return (

        <>
            <Skeleton />
            <Skeleton animation={false} />
            <Skeleton animation="wave" />
        </>
    );
}

export default memo(observer(LoadingTabSkeleton));