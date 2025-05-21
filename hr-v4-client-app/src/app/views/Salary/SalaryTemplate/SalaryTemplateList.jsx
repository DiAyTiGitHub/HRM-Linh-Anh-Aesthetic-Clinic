import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import CheckIcon from '@material-ui/icons/Check';
import SalaryTemplatePopupClon from "./SalaryTemplatePopupClon";
import ControlPointDuplicateIcon from '@material-ui/icons/ControlPointDuplicate';

function SalaryTemplateList() {
    const { salaryTemplateStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        listSalaryTemplates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenPopUpClon,
        handleOpenView
    } = salaryTemplateStore;

    const {
        isAdmin,
        isManager,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    const history = useHistory();

    function handleUpdateSalaryTemplate(template) {
        history.push(ConstantList.ROOT_PATH + `salary-template/` + template?.id);
    }

    function handleOpenViewSalaryTemplate(template) {
        handleOpenView(template?.id)
        history.push(ConstantList.ROOT_PATH + `salary-template/view/` + template?.id);
    }

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "100px",
            align: "center",
            render: (rowData) => (
                <div className="flex flex-middle justify-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Xem chi tiết mẫu bảng lương"}
                    >
                        <IconButton
                            size="small"
                            onClick={() => handleOpenViewSalaryTemplate(rowData)}
                        >
                            <Icon fontSize="small" style={{ color: "green" }}>
                                remove_red_eye
                            </Icon>
                        </IconButton>

                    </Tooltip>
                    {(isCompensationBenifit) && (
                        <Tooltip
                            arrow
                            title="Cập nhật mẫu bảng lương"
                            placement="top"
                        >
                            <IconButton size="small" onClick={() => handleUpdateSalaryTemplate(rowData)}>
                                <Icon fontSize="small" color="primary">edit</Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {(isCompensationBenifit) && (
                        <Tooltip
                            arrow
                            title="Xóa mẫu bảng lương"
                            placement="top"
                        >
                            <IconButton className="ml-4" size="small" onClick={() => handleDelete(rowData)}>
                                <Icon fontSize="small" color="secondary">delete</Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {(isCompensationBenifit) && (
                        <Tooltip
                            arrow
                            title="Nhân bản mẫu bảng lương được chọn"
                            placement="top"
                        >
                            <IconButton
                                className="ml-4"
                                size="small"
                                onClick={() => handleOpenPopUpClon(rowData)}
                            >
                                <ControlPointDuplicateIcon fontSize="small" color="primary" />
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
            ),
        },
        {
            title: "Mã mẫu",
            field: "code",
            width: "20%",
            render: data => (
                <Tooltip
                    arrow
                    placement="top"
                    title={data?.code}
                >
                    <span className="px-6 multiline-ellipsis">
                        {data?.code}
                    </span>
                </Tooltip>
            )
        },
        {
            title: "Tên mẫu bảng lương",
            width: "30%",
            field: "name",
        },
        // {
        //     title: "Mô tả",
        //     width: "30%",
        //     field: "description",
        //     minWidth: "150px",
        // },
        // {
        //     title: "Số thành phần",
        //     width: "10%",
        //     field: "numberOfItems",
        //     minWidth: "150px",
        // },
        {
            title: "Cập nhật cuối",
            width: "10%",
            align: "center",
            field: "birthDate",
            render: (rowData) => (
                <span className="px-6">{rowData?.modifiedDate && formatDate("DD/MM/YYYY", rowData?.modifiedDate)}</span>
            ),
        },
        {
            title: "Tạo phiếu lương",
            field: "isCreatePayslip",
            width: "8%",
            align: "center",
            render: (data) => (data?.isCreatePayslip ? <CheckIcon fontSize="small" style={{ color: "green" }} /> : ""),
        }
    ];


    return (<>
        <GlobitsTable
            selection={(isAdmin || isManager || isCompensationBenifit)}
            data={listSalaryTemplates}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 20, 25, 50]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />

        <SalaryTemplatePopupClon />
    </>);
}

export default memo(observer(SalaryTemplateList));
