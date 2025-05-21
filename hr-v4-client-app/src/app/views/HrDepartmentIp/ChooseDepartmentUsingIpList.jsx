import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import GlobitsPagination from "../../common/GlobitsPagination";
import MaterialTable from "material-table";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Radio } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { useFormikContext } from "formik";

function ChooseDepartmentUsingIpList(props) {
    const { handleClose } = props;
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
        updatePageData
    } = departmentStore;

    const { setFieldValue, values } = useFormikContext();

    function handleSelectItem(_, department) {
        if (values?.department && values?.department?.id === department?.id) {
            setFieldValue("department", null);
        } else {
            setFieldValue("department", department);
            handleClose();
        }
    };

    const columns = [
        {
            title: t("general.popup.select"),
            render: (rowData) => (
                <Radio
                    id={`radio${rowData?.id}`}
                    name="radSelected"
                    value={rowData?.id}
                    checked={values?.department?.id === rowData?.id}
                    onClick={(event) => handleSelectItem(event, rowData)}
                />
            ),
        },
        {
            title: t("department.code"),
            field: "code",
            ...Config.tableCellConfig,
        },
        { title: t("department.name"), field: "name", ...Config.tableCellConfig },
        {
            title: t("department.description"),
            field: "description",
            ...Config.tableCellConfig,
        },
    ];

    useEffect(function () {
        const initializeSearchObject = {
            pageSize: 10,
            pageIndex: 1
        };
        updatePageData(initializeSearchObject);
    }, []);

    return (
        <div className="w-100 pt-8">
            <MaterialTable
                data={departmentList}
                columns={columns}
                parentChildData={(row, rows) => {
                    var list = rows.find((a) => a?.id === row?.parentId);
                    return list;
                }}
                options={{
                    selection: false,
                    actionsColumnIndex: -1,
                    paging: false,
                    search: false,
                    toolbar: false,
                    maxBodyHeight: "480px",
                    headerStyle: {
                        backgroundColor: "#2f4f4f",
                        color: "#fff",
                    },
                    rowStyle: (rowData, index) => ({
                        backgroundColor: index % 2 === 1 ? "rgb(237, 245, 251)" : "#FFF",
                    }),
                }}
                onSelectionChange={(rows) => {
                    this.data = rows;
                }}
            />
            <GlobitsPagination
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setRowsPerPage}
                pageSize={rowsPerPage}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={page}
            />
        </div>
    );
}

export default memo(observer(ChooseDepartmentUsingIpList));
