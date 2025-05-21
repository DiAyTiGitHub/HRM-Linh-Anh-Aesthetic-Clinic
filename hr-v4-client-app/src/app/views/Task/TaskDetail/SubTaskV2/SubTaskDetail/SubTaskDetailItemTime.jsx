import { observer } from "mobx-react";
import { React, memo, useState } from "react";
import { Tooltip } from '@material-ui/core'
import { formatDate } from 'app/LocalFunction';
import ScheduleIcon from '@material-ui/icons/Schedule';
import ChooseDateSubTaskDetailPopover from "../Popover/ChooseDateSubTaskDetailPopover";

function SubTaskDetailItemTime(props) {
    const {
        detail,
        detailItemIndex,
        subTaskIndex,

    } = props;

    const [anchorEl, setAnchorEl] = useState(null);

    function handleOpenChooseTime(event) {
        setAnchorEl(event.currentTarget);
    }

    function handleCloseChooseTime() {
        setAnchorEl(null);
    }

    return (
        <>
            <Tooltip title={"Đặt thời gian"} placement="top">
                <div className="iconWrapper" onClick={handleOpenChooseTime}>
                    <ScheduleIcon
                        className="my-0 mx-4 subTaskDetailIcon"
                    />
                </div>
            </Tooltip>


            {Boolean(anchorEl) && (
                <ChooseDateSubTaskDetailPopover
                    anchorEl={anchorEl}
                    detailItemIndex={detailItemIndex}
                    subTaskIndex={subTaskIndex}
                    handleCloseChooseTime={handleCloseChooseTime}
                />
            )}
        </>
    );
}

export default memo(observer(SubTaskDetailItemTime));