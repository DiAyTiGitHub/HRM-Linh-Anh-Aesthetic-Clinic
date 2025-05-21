import { Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import {
    IconButton,
    Icon,
    Button
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import ChooseSalaryConfigDepartmentPopup from "./ChooseSalaryConfigDepartmentPopup";

function SalaryConfigDepartmentTable() {
    const { positionTitleV2Store, departmentStore } = useStore();
    const { t } = useTranslation();

    const {
        departmentList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        getTitleType
    } = departmentStore;

    const { values, setFieldValue } = useFormikContext();

    function handleDeleteDepartment(data) {
        const newList = [...values?.departments];

        let deleteIndex = -1;
        for (let i = 0; i < newList?.length; i++) {
            const item = newList[i];
            if (item?.id == data?.id) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex != -1) newList.splice(deleteIndex, 1);
        setFieldValue("departments", newList);
    }

    const columns = [
        {
            title: t("department.code"),
            field: "code",
            align: "left",
        },
        {
            title: t("department.name"),
            field: "name",
            align: "left",
        },
        {
            title: t("department.shortName"),
            field: "shortName",
            align: "left",
        },
        {
            title: t("department.industryBlock"),
            field: "industryBlock",
            align: "left",
        },
        {
            title: t("general.action"),
            minWidth: "48px",
            align: "center",
            render: (rowData) => (
                <Tooltip placement="top" title="Loại bỏ">
                    <IconButton size="small" onClick={() => handleDeleteDepartment(rowData)}>
                        <Icon fontSize="small" color="secondary">
                            delete
                        </Icon>
                    </IconButton>
                </Tooltip>
            ),
        },
    ];

    const [openChooseDepartmentPopup, setOpenChooseDepartmentPopup] = useState(false);
    function handleOpenPoup() {
        setOpenChooseDepartmentPopup(true);
    }

    function handleClosePopup() {
        setOpenChooseDepartmentPopup(false);
    }

    return (
        <>
            <div className="flex justify-between items-center py-8">
                <div className="d-block">
                    <strong className="">
                        Danh sách đơn vị áp dụng:
                    </strong>
                </div>

                <Button
                    startIcon={<AddIcon />}
                    type="button"
                    onClick={handleOpenPoup}
                    className="btn bgc-lighter-dark-green text-white d-inline-flex"
                >
                    Thêm đơn vị
                </Button>
            </div>

            <GlobitsTable
                data={values?.departments || []}
                columns={columns}
                nonePagination
            />

            {
                openChooseDepartmentPopup && (
                    <ChooseSalaryConfigDepartmentPopup
                        openPopup={openChooseDepartmentPopup}
                        handleClosePopup={handleClosePopup}
                    />
                )
            }
        </>
    );
}

export default memo(observer(SalaryConfigDepartmentTable));