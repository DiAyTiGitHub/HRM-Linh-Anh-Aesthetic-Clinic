import { Grid, Icon, IconButton, Tooltip, makeStyles } from "@material-ui/core";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import MulPositionsPopup from "./components/PositionList/MulPositionsPopup";
const useStyles = makeStyles((theme) => ({
    widthTh: {
        "& th": {
            minWidth: "150px",
        },
    },
    tableContainer: {
        overflowX: "auto",
    },
}));
function HrResourcePlanReportList() {
    const { hrResourcePlanReportStore, hrRoleUtilsStore, departmentStore, positionTitleStore } = useStore();
    const { t } = useTranslation();

    const { hrResourcePlanReportList, totalPages, totalElements, searchObject, handleChangePage, setPageSize } =
        hrResourcePlanReportStore;
    const { isAdmin, checkAllUserRoles } = hrRoleUtilsStore;

    const { selectedDepartment, handleSelectDepartment } = departmentStore;
    const { selectedPositionTitle, handleSelectPositionTitle } = positionTitleStore;
    useEffect(() => {
        checkAllUserRoles();
    }, []);
    const classes = useStyles();
    const [openPositionPopup, setOpenPositionPopup] = useState(false);
    const handleOpenPositionPopupByDepartment = (department) => {
        const departmentDto = { id: department?.departmentId, name: department?.departmentName };
        console.log(departmentDto);
        handleSelectDepartment(departmentDto);
        handleOpenPositionPopup();
    };
    const handleOpenPositionPopupByPositionTitle = (pos, department) => {
        const departmentDto = { id: department?.departmentId, name: department?.departmentName };
        const positionTitle = { id: pos?.positionTitleId, name: pos?.positionTitleName };
        console.log(departmentDto);
        console.log(positionTitle);
        handleSelectDepartment(departmentDto);
        handleSelectPositionTitle(positionTitle);
        handleOpenPositionPopup();
    };
    const handleOpenPositionPopup = () => {
        setOpenPositionPopup(true);
    };
    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <section className={`commonTableContainer ${classes.tableContainer}`}>
                    <table className={`commonTable w-100`}>
                        <TableHeader />
                        <tbody>
                            {hrResourcePlanReportList?.map((department, deptIndex) => (
                                <React.Fragment key={department.departmentId}>
                                    <tr style={{ backgroundColor: "#e0f7fa", fontWeight: "bold" }}>
                                        <td className="px-6">
                                            {deptIndex + 1}. {department.departmentName}
                                        </td>
                                        <td align='center'>{department.nominalQuantity}</td>
                                        <td align='center'>{department.actualQuantity}</td>
                                        <td align='center'>{department.supplementaryQuantity}</td>
                                        <td align='center'>{department.filteredQuantity}</td>
                                        <td align='center'>
                                            <Tooltip arrow placement='top' title={"Xem chi tiết vị trí"}>
                                                <IconButton
                                                    className='ml-4'
                                                    size='small'
                                                    onClick={() => handleOpenPositionPopupByDepartment(department)}>
                                                    <Icon fontSize='small' style={{ color: "green" }}>
                                                        remove_red_eye
                                                    </Icon>
                                                </IconButton>
                                            </Tooltip>
                                        </td>
                                    </tr>

                                    {department.positionTitles?.map((pos, posIndex) => (
                                        <tr key={pos.positionTitleId + posIndex}>
                                            <td className="pl-18">{`${deptIndex + 1}.${posIndex + 1}. ${pos.positionTitleName}`}</td>
                                            <td align='center'>{pos.nominalQuantity}</td>
                                            <td align='center'>{pos.actualQuantity}</td>
                                            <td align='center'>{pos.supplementaryQuantity}</td>
                                            <td align='center'>{pos.filteredQuantity}</td>
                                            <td align='center'>
                                                <Tooltip arrow placement='top' title={"Xem chi tiết vị trí"}>
                                                    <IconButton
                                                        className='ml-4'
                                                        size='small'
                                                        onClick={() =>
                                                            handleOpenPositionPopupByPositionTitle(
                                                                pos,
                                                                department
                                                            )
                                                        }>
                                                        <Icon fontSize='small' style={{ color: "green" }}>
                                                            remove_red_eye
                                                        </Icon>
                                                    </IconButton>
                                                </Tooltip>
                                            </td>
                                        </tr>
                                    ))}
                                </React.Fragment>
                            ))}
                        </tbody>
                    </table>
                </section>
            </Grid>

            {openPositionPopup && (
                <MulPositionsPopup
                    open={openPositionPopup}
                    handleClose={() => {
                        handleSelectDepartment(null);
                        handleSelectPositionTitle(null);
                        setOpenPositionPopup(false);
                    }}
                />
            )}
        </Grid>
    );
}

const TableHeader = memo(() => {
    const classes = useStyles();
    return (
        <thead>
            <tr className={`${classes.widthTh} tableHeader`}>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Phòng ban/Chức danh
                </th>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Số lượng định biên
                </th>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Số lượng thực tế
                </th>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Số lượng cần bổ sung
                </th>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Số lượng cần lọc
                </th>
                <th align='center' className='stickyColumn stickyHeader displayNameStaffSWS-column'>
                    Xem vị trí
                </th>
            </tr>
        </thead>
    );
});

export default memo(observer(HrResourcePlanReportList));
