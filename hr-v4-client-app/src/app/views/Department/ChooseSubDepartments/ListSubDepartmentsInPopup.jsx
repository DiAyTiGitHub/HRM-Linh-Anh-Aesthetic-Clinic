import React, { memo } from "react";
import { Radio } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import GlobitsTable from "app/common/GlobitsTable";

function ListSubDepartmentsInPopup(props) {
    const { chosenList, setChosenList } = props;

    const { t } = useTranslation();
    const { departmentStore } = useStore();
    const {
        pagingSubDeparments,
        handleChangePageSubDepartments,
        handleSetSearchObjectSubDepartments,
        totalElementsSubDepartments,
        totalPagesSubDepartments,
        resetStoreSubDepartments,
        searchObjectSubDepartments,
        listSubDepartments,
        setPageSizeSubDeparments
    } = departmentStore;

    const { values, setFieldValue } = useFormikContext();

    function isChosen(rowData) {
        // console.log("chosenList", chosenList);
        return Array.from(chosenList)?.some(item => item?.id === rowData?.id);
    }

    function handleChoose(rowData) {
        const itemIndex = Array.from(chosenList)?.findIndex(item => item?.id === rowData?.id);
        const newChosenList = [...chosenList];

        if (itemIndex === -1) {
            // Item is not chosen yet => add it to chosen list
            newChosenList.push(rowData);
        } else {
            // Item is already chosen => remove it from chosen list
            newChosenList.splice(itemIndex, 1);
        }

        setChosenList([...newChosenList]);
    }

    const columns = [
        {
            title: t("general.action"),
            render: (rowData) => {
                const chosen = isChosen(rowData);

                return (
                    <Radio
                        className="pr-8"
                        id={`radio${rowData?.id}`}
                        name="radSelected"
                        value={rowData?.id}
                        checked={chosen}
                        onClick={() => handleChoose(rowData)}
                        disabled={values?.id == rowData?.id || values?.parent?.id == rowData?.id}
                    />
                );
            },
        },
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
    ];

    return (
        <GlobitsTable
            data={listSubDepartments}
            columns={columns}
            totalPages={totalPagesSubDepartments}
            handleChangePage={handleChangePageSubDepartments}
            setRowsPerPage={setPageSizeSubDeparments}
            pageSize={searchObjectSubDepartments?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElementsSubDepartments}
            page={searchObjectSubDepartments?.pageIndex}
        />
    );
}

export default memo(observer(ListSubDepartmentsInPopup));
