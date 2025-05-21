import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { NavLink } from "react-router-dom";
import ConstantList from "app/appConfig";

function StaffLabourManagementBookList() {
    const {
        staffLabourManagementBookStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        staffManagementBookList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleOpenView
    } = staffLabourManagementBookStore;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => (
                <div className="flex flex-center px-4">
                    <Tooltip
                        placement="top"
                        arrow
                        title="Xem thông tin nhân sự"
                    >
                        <IconButton
                            style={{ color: "green" }}
                            component={NavLink} size="small"
                            to={ConstantList.ROOT_PATH + "staff/profile/" + rowData.staffId}
                        >
                            <Icon fontSize="small" color="primary">visibility</Icon>
                        </IconButton>
                    </Tooltip>

                    <Tooltip
                        placement="top"
                        arrow
                        title="Cập nhật thông tin nhân sự"
                    >
                        <IconButton
                            className="ml-4"
                            component={NavLink}
                            size="small" to={ConstantList.ROOT_PATH + "staff/edit/" + rowData.staffId}>
                            <Icon fontSize="small" color="primary">edit</Icon>
                        </IconButton>
                    </Tooltip>
                </div>
            ),
        },
        { title: "Họ và tên", field: "displayName", width: "15%", minWidth: "120px" },
        { title: "Giới tính", field: "gender", width: "10%", minWidth: "120px" },
        { title: "Năm sinh", field: "birthDate", width: "10%", minWidth: "120px" },
        { title: "Quốc tịch", field: "nationalityName", minWidth: "120px" },
        { title: "Địa chỉ", field: "staffAddress", minWidth: "120px" },
        { title: "CCCD/Hộ chiếu", field: "staffIndentity", minWidth: "120px" },
        { title: "Trình độ CMKT", field: "staffEducationDegree", minWidth: "120px" },
        { title: "Cấp bậc", field: "rankTitleJoined", minWidth: "120px" },
        { title: "Vị trí làm việc", field: "titleJoined", minWidth: "120px" },
        { title: "Loại HĐLĐ", field: "contractType", minWidth: "120px" },
        { title: "Ngày bắt đầu làm việc", field: "signDateContract", minWidth: "120px" },
        { title: "BHXH", field: "bhxhSalary", minWidth: "120px" },
        { title: "BHYT", field: "bhytSalary", minWidth: "120px" },
        { title: "BHTN", field: "bhtnSalary", minWidth: "120px" },
        { title: "Lương cơ bản", field: "insuranceSalaryStr", minWidth: "120px" },
        { title: "Nâng bậc, nâng lương", field: "upSalaryInfo", minWidth: "120px" },
        { title: "Số ngày nghỉ/năm", field: "leaveDays", minWidth: "120px" },
        { title: "Giờ làm thêm", field: "otHours", minWidth: "120px" },
        { title: "Hưởng chế độ BH", field: "socialInsuranceBenefitEligible", minWidth: "120px" },
        { title: "Đào tạo, nâng cao tay nghề", field: "studyInfo", minWidth: "120px" },
        { title: "Kỷ luật, trách nhiệm vật chất", field: "disciplineInfo", minWidth: "120px" },
        { title: "Tai nạn lao động, bệnh nghề nghiệp", field: "occupationalAccidentInfo", minWidth: "120px" },
        { title: "Thời điểm & lý do chấm dứt HĐLĐ", field: "endDateContract", minWidth: "120px" }
    ];


    return (
        <GlobitsTable
            selection
            data={staffManagementBookList}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(StaffLabourManagementBookList));
