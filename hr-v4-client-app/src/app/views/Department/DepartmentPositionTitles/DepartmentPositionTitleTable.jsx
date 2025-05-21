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
import ChooseDepartmentPositionTitlePopup from "./ChooseDepartmentPositionTitlePopup";
import AddIcon from "@material-ui/icons/Add";

function DepartmentPositionTitleTable() {
    const { positionTitleV2Store, departmentStore } = useStore();
    const { t } = useTranslation();

    const {
        listPositionTitle,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        getTitleType
    } = positionTitleV2Store;

    const {
        openChoosePSPopup,
        setOpenChoosePSPopup
    } = departmentStore;

    const { values, setFieldValue } = useFormikContext();

    function handleDeletePositionTitle(data) {
        const newList = [...values?.positionTitles];

        let deleteIndex = -1;
        for (let i = 0; i < newList?.length; i++) {
            const positionTitle = newList[i];
            if (positionTitle?.id == data?.id) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex != -1) newList.splice(deleteIndex, 1);
        setFieldValue("positionTitles", newList);
    }

    const columns = [
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên chức danh",
            field: "name",
            align: "left",
        },
        {
            title: "Tên viết tắt",
            field: "shortName",
            align: "left",
        },
        {
            title: "Loại",
            field: "type",
            align: "left",
            render: data => {
                const displayType = getTitleType(data?.type);

                return (
                    <span>{displayType}</span>
                );
            }
        },
        // {
        //     title: "Hệ số phụ cấp",
        //     field: "positionCoefficient",
        //     align: "left",
        // },
        // {
        //     title: "Cấp bậc",
        //     field: "rankTitle.name",
        //     align: "left",
        // },
        // {
        //   title: "Nhóm quyền mặc định",
        //   field: "positionRole.name",
        //   align: "left",
        // }, 
        // {
        //   title: "Mô tả",
        //   field: "description",
        //   align: "left",
        // },
        {
            title: t("general.action"),
            minWidth: "48px",
            align: "center",
            render: (rowData) => (
                <Tooltip placement="top" title="Loại bỏ">
                    <IconButton size="small" onClick={() => handleDeletePositionTitle(rowData)}>
                        <Icon fontSize="small" color="secondary">
                            delete
                        </Icon>
                    </IconButton>
                </Tooltip>
            ),
        },
    ];

    function handleOpenChoosePSPopup() {
        setOpenChoosePSPopup(true);
    }

    function handleCloseChoosePSPopup() {
        setOpenChoosePSPopup(false);
    }

    return (
        <>
            <div className="flex justify-between items-center py-8">
                <div className="d-block">
                    <strong className="">
                        {t("department.positionTitles")}:
                    </strong>
                </div>

                <Button
                    startIcon={<AddIcon />}
                    type="button"
                    onClick={handleOpenChoosePSPopup}
                    className="btn bgc-lighter-dark-green text-white d-inline-flex"
                >
                    Thêm chức danh
                </Button>
            </div>

            <GlobitsTable
                data={values?.positionTitles || []}
                columns={columns}
                nonePagination
            />

            {
                openChoosePSPopup && (
                    <ChooseDepartmentPositionTitlePopup
                    />
                )
            }
        </>
    );
}

export default memo(observer(DepartmentPositionTitleTable));