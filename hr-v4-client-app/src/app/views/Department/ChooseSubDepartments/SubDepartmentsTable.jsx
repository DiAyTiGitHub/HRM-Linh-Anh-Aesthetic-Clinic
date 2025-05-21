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
import ChooseSubDepartmentsPopup from "./ChooseSubDepartmentsPopup";
import AddIcon from "@material-ui/icons/Add";

function SubDepartmentsTable() {
    const { departmentStore } = useStore();
    const { t } = useTranslation();

    const {
        openChooseSubDpmPopup,
        setOpenChooseSubDpmPopup
    } = departmentStore;

    const { values, setFieldValue } = useFormikContext();

    function handleDeleteSubDepartment(data) {
        const newList = [...values?.children];

        let deleteIndex = -1;
        for (let i = 0; i < newList?.length; i++) {
            const child = newList[i];
            if (child?.id == data?.id) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex != -1) newList.splice(deleteIndex, 1);
        setFieldValue("children", newList);
    }

    const columns = [
        {
            title: t("department.code"),
            field: "code",
        },
        {
            title: t("department.name"),
            field: "name",
        },
        {
            title: t("department.shortName"),
            field: "shortName",
            render: data => data?.shortName
        },
        // {
        //     title: t("department.industryBlock"),
        //     field: "industryBlock",
        // },
        {
            title: t("department.hrdepartmentType"),
            field: "hrDepartmentType.name",
        },
        // {
        //     title: t("department.description"),
        //     field: "description",
        //     ...Config.tableCellConfig,
        // },
        {
            title: t("general.action"),
            minWidth: "48px",
            align: "center",
            render: (rowData) => (
                <Tooltip placement="top" title="Loại bỏ">
                    <IconButton size="small" onClick={() => handleDeleteSubDepartment(rowData)}>
                        <Icon fontSize="small" color="secondary">
                            delete
                        </Icon>
                    </IconButton>
                </Tooltip>
            ),
        },
    ];

    function handleOpenChooseSubDpmPopup() {
        setOpenChooseSubDpmPopup(true);
    }

    return (
        <>
            <div className="flex justify-between items-center py-8">
                <div className="d-block">
                    <strong className="">
                        {t("department.subDepartments")}:
                    </strong>
                </div>

                <Button
                    startIcon={<AddIcon />}
                    type="button"
                    onClick={handleOpenChooseSubDpmPopup}
                    className="btn bgc-lighter-dark-green text-white d-inline-flex"
                >
                    Thêm đơn vị con
                </Button>
            </div>

            <GlobitsTable
                data={values?.children || []}
                columns={columns}
                nonePagination
            />

            {
                openChooseSubDpmPopup && (
                    <ChooseSubDepartmentsPopup
                    />
                )
            }
        </>
    );
}

export default memo(observer(SubDepartmentsTable));