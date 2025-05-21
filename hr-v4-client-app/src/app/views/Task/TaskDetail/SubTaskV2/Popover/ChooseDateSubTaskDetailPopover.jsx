import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import Popover from '@material-ui/core/Popover';
import { observer } from "mobx-react";
import { Icon, IconButton } from "@material-ui/core";

function ChooseDateSubTaskDetailPopover(props) {
    const {
        anchorEl,
        detailItemIndex,
        subTaskIndex,
        handleCloseChooseTime
    } = props;

    const { t } = useTranslation();

    return (
        <Popover
            anchorEl={anchorEl}
            id={'simple-popove-time'}
            open={Boolean(anchorEl)}
            onClose={handleCloseChooseTime}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'center',
            }}
            transformOrigin={{
                vertical: 'top',
                horizontal: 'center',
            }}
        >
            <div className="p-10">
                <div className="mb-1- pb-5" style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
                    <p className="text-center m-0">{t("task.time.title")}</p>
                    <IconButton
                        style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
                        onClick={handleCloseChooseTime}
                    >
                        <Icon color="disabled" title={"Đóng"} fontSize="small" >
                            close
                        </Icon>
                    </IconButton>
                </div>

                <div style={{ color: "#5e6c84" }}>{t("task.time.startTime")}</div>
                <GlobitsDateTimePicker disableFuture name={`subTasks[${subTaskIndex}].items[${detailItemIndex}].startTime`} isDateTimePicker />
                <div style={{ color: "#5e6c84" }}>{t("task.time.endTime")}</div>
                <GlobitsDateTimePicker disableFuture name={`subTasks[${subTaskIndex}].items[${detailItemIndex}].endTime`} isDateTimePicker />
            </div>
        </Popover>

    );
}

export default memo(observer(ChooseDateSubTaskDetailPopover));
