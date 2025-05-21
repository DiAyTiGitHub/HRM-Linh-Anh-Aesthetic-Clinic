import { observer } from "mobx-react";
import { React, memo, useState } from "react";
import { Tooltip } from '@material-ui/core'
import PersonAddIcon from '@material-ui/icons/PersonAdd';
import GlobitsAvatar from 'app/common/GlobitsAvatar';
import ChooseAssigneeSubTaskDetailPopover from "../Popover/ChooseAssigneeSubTaskDetailPopover";

function SubTaskDetailItemDoer(props) {
    const [anchorEl, setAnchorEl] = useState(null);

    function handleOpenChooseDoer(event) {
        setAnchorEl(event.currentTarget);
    }

    function handleCloseChooseDoer() {
        setAnchorEl(null);
    }

    const {
        detail,
        detailItemIndex,
        subTaskIndex,

    } = props;

    let tooltipContent = "Chưa có người thực hiện";
    if (detail?.staffs && detail?.staffs?.length > 0) {
        tooltipContent = `Thực hiện: ${detail?.staffs[0]?.displayName}`;
    }

    return (
        <>
            <Tooltip title={tooltipContent} placement="top">
                <div className="iconWrapper" onClick={handleOpenChooseDoer}>
                    {detail?.staffs && Array.isArray(detail?.staffs) && detail?.staffs?.length > 0 ? (
                        <GlobitsAvatar
                            className="my-0 mx-4"
                            style={{ width: '20px', height: 20, borderRadius: '50%' }}
                            imgPath={detail?.staffs[0]?.imagePath} name={detail?.staffs[0]?.displayName}
                        />
                    ) : (
                        <PersonAddIcon
                            className="my-0 mx-4 subTaskDetailIcon"
                        />
                    )}


                </div>
            </Tooltip>


            {Boolean(anchorEl) && (
                <ChooseAssigneeSubTaskDetailPopover
                    anchorEl={anchorEl}
                    detailItemIndex={detailItemIndex}
                    subTaskIndex={subTaskIndex}
                    handleCloseChooseDoer={handleCloseChooseDoer}
                />
            )}
        </>
    );
}

export default memo(observer(SubTaskDetailItemDoer));