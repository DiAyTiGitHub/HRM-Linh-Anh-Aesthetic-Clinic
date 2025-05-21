import { Radio } from '@material-ui/core';
import GlobitsTable from 'app/common/GlobitsTable';
import { useStore } from 'app/stores';
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useTranslation } from 'react-i18next';

function ChooseListDepartmentInSalaryConfig(props) {
    const { departmentStore } = useStore();
    const { t } = useTranslation();

    const {
        departmentList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleSelectListDepartment,
    } = departmentStore;

    const { values, setFieldValue } = useFormikContext();

    function isChosen(rowData) {
        const chosenList = values?.departments || [];
        return chosenList.some(item => item?.id === rowData?.id);
    }

    function handleChoose(rowData) {
        const chosenList = values?.departments || [];
        const itemIndex = chosenList.findIndex(item => item?.id === rowData?.id);

        if (itemIndex === -1) {
            // Item is not chosen yet => add it to chosen list
            chosenList.push(rowData);
        } else {
            // Item is already chosen => remove it from chosen list
            chosenList.splice(itemIndex, 1);
        }

        setFieldValue("departments", [...chosenList]);
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
            // selection
            // handleSelectList={handleSelectListDepartment}
            data={departmentList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
        />
    );
}

export default memo(observer(ChooseListDepartmentInSalaryConfig));