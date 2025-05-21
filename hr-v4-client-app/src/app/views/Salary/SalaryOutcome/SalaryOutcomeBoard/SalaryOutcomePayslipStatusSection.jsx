import { Icon, IconButton, Tooltip } from "@material-ui/core";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import React, { memo } from "react";

function SalaryOutcomePayslipStatusSection(props) {
    const {
        resultStaff
    } = props;

    let isLocked = false;
    let isApproved = false;
    let isNotApproved = false;
    let isNotApprovedYet = false;

    if (resultStaff?.approvalStatus == LocalConstants.SalaryStaffPayslipApprovalStatus.LOCKED.value) {
        isLocked = true;
    }
    else if (resultStaff?.approvalStatus == LocalConstants.SalaryStaffPayslipApprovalStatus.APPROVED.value) {
        isApproved = true;
    }
    else if (resultStaff?.approvalStatus == LocalConstants.SalaryStaffPayslipApprovalStatus.NOT_APPROVED.value) {
        isNotApproved = true;
    }
    else if (resultStaff?.approvalStatus == LocalConstants.SalaryStaffPayslipApprovalStatus.NOT_APPROVED_YET.value) {
        isNotApprovedYet = true;
    }

    return (
        <React.Fragment>
            {isLocked && (
                <Tooltip
                    title={"Phiếu lương đã được chốt"}
                    placement="top"
                    arrow
                >
                    <IconButton className='text-green' size='small'>
                        <Icon fontSize='small' color='green'>
                            lock
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}

            {isNotApproved && (
                <Tooltip
                    title={"Phiếu lương Không được duyệt"}
                    placement="top"
                    arrow
                >
                    <IconButton className='text-red' size='small'>
                        <Icon fontSize='small' color='red'>
                            close
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}

            {isNotApprovedYet && (
                <Tooltip
                    title={"Phiếu lương Chưa được duyệt"}
                    placement="top"
                    arrow
                >
                    <IconButton className='text-primary' size='small'>
                        <Icon fontSize='small' color='blue'>
                            hourglass_empty
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}

            {isApproved && (
                <Tooltip
                    title={"Phiếu lương Đã được duyệt"}
                    placement="top"
                    arrow
                >
                    <IconButton className='text-green' size='small'>
                        <Icon fontSize='small' color='blue'>
                            done_all
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}
        </React.Fragment>
    );
}

export default memo(observer(SalaryOutcomePayslipStatusSection));