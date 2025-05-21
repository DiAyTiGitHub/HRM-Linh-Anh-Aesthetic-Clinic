import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { NavLink } from "react-router-dom";
import ConstantList from "app/appConfig";

function StaffLabourUtilReportList() {
    const {
        staffLabourUtilReportStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        staffLabourUtilReportList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleOpenCreateEdit,
        handleSelectListDelete,
        handleOpenView
    } = staffLabourUtilReportStore;

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
        // { title: "STT", field: "orderNumber", width: "15%", minWidth: "120px" },
        { title: "Họ và tên", field: "displayName", width: "15%", minWidth: "120px" },
        { title: "Mã số BHXH", field: "socialInsuranceNumber", width: "15%", minWidth: "120px" },
        { title: "Ngày sinh", field: "birthDate", width: "10%", minWidth: "120px" },
        { title: "Giới tính", field: "gender", width: "10%", minWidth: "120px" },
        { title: "Số CCCD/ CMND/ Hộ chiếu", field: "staffIndentity", width: "10%", minWidth: "120px" },
        { title: "Cấp bậc, chức vụ, chức danh nghề, nơi làm việc", field: "rankTitleJoined", width: "10%", minWidth: "120px" },
        {
            title: "Vị trí việc làm - Nhà quản lý",
            field: "isManager", minWidth: "120px"
        },
        {
            title: "Vị trí việc làm - Chuyên môn kỹ thuật bậc cao",
            field: "highTechQualification", minWidth: "120px"
        },
        {
            title: "Vị trí việc làm - Chuyên môn kỹ thuật bậc trung",
            field: "midTechQualification", minWidth: "120px"
        },
        {
            title: "Vị trí việc làm - Khác",
            field: "otherQualification", minWidth: "120px"
        },
        {
            title: "Hệ số/ Mức lương",
            field: "salaryCoefficient", minWidth: "120px"
        },
        {
            title: "Phụ cấp chức vụ",
            field: "positionAllowance", minWidth: "120px"
        },
        // {
        //     title: "Phụ cấp thâm niên VK",
        //     field: "", minWidth: "120px"
        // },
        // {
        //     title: "Phụ cấp thâm niên nghề",
        //     field: "", minWidth: "120px"
        // },
        // {
        //     title: "Phụ cấp lương",
        //     field: "", minWidth: "120px"
        // },
        // {
        //     title: "Phụ cấp các khoản bổ sung",
        //     field: "", minWidth: "120px"
        // },

        // {
        //     title: "Ngày bắt đầu - Ngành/nghề nặng nhọc, độc hại",
        //     field: "toxicAllowanceStartDate", minWidth: "120px"
        // },
        // {
        //     title: "Ngày kết thúc - Ngành/nghề nặng nhọc, độc hại",
        //     field: "toxicAllowanceEndDate", minWidth: "120px"
        // },
        {
            title: "Ngày bắt đầu HĐLĐ không xác định thời hạn",
            field: "indefiniteContractStartDate", minWidth: "120px"
        },
        {
            title: "Ngày bắt đầu - Hiệu lực HĐLĐ xác định thời hạn ",
            field: "definiteContractStartDate", minWidth: "120px"
        },
        {
            title: "Ngày kết thúc - Hiệu lực HĐLĐ xác định thời hạn ",
            field: "definiteContractEndDate", minWidth: "120px"
        },
        {
            title: "Ngày bắt đầu - Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc)",
            field: "otherContractStartDate", minWidth: "120px"
        },
        {
            title: "Ngày kết thúc - Hiệu lực HĐLĐ khác (dưới 1 tháng, thử việc)",
            field: "otherContractEndDate", minWidth: "120px"
        },

        { title: "Thời điểm đơn vị bắt đầu đóng BHXH", field: "startSocialInsuranceDate", minWidth: "120px" },
        { title: "Thời điểm đơn vị kết thúc đóng BHXH", field: "endSocialInsuranceDate", minWidth: "120px" },
        { title: "Ghi chú", field: "note", minWidth: "120px" },
    ];


    return (
        <GlobitsTable
            selection
            data={staffLabourUtilReportList}
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

export default memo(observer(StaffLabourUtilReportList));
