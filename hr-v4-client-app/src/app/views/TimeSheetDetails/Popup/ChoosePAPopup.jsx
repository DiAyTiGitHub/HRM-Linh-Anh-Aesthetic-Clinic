import React, { memo, useEffect } from "react";
import { Grid, } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { Form, Formik, useFormikContext } from "formik";
import GlobitsPopup from "app/common/GlobitsPopup";
import { Radio } from "@material-ui/core";
import { getChildren, ToAlphabet } from "app/LocalFunction";
import MaterialTable from "material-table";
import { ArrowUpward, ChevronRight } from "@material-ui/icons";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import SearchIcon from "@material-ui/icons/Search";
import { useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { IconButton } from "@material-ui/core";
import Search from "@material-ui/icons/Search";


function ChoosePAPopup(props) {
    const { handleClose, open } = props;
    const [listActivity, setListActivity] = useState([]);

    const { t } = useTranslation();

    const { projectStore } = useStore();
    const { getListActivityById } = projectStore;

    const { values, setFieldValue } = useFormikContext();


    useEffect(() => {
        if (values?.project?.id) {
            getListActivityById(values?.project?.id).then((data) => {
                setListActivity(data);
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [values?.project?.id]);

    const handleSelectActivity = (activity) => {
        if (values?.projectActivity?.id === activity?.id) {
            setFieldValue("projectActivity", null);
        } else {
            setFieldValue("projectActivity", activity);
            handleClose();
        }
    };

    const columns = [
        {
            title: t("general.popup.select"),
            align: "center",
            cellStyle: {
                textAlign: "center",
                display: "flex",
                justifyContent: "center"
            },
            render: (rowData) => (
                <Radio
                    className="pr-8"
                    id={`radio${rowData?.id}`}
                    name="radSelected"
                    value={rowData.id}
                    checked={values?.projectActivity?.id ? values?.projectActivity?.id === rowData?.id : false}
                    onClick={(event) => handleSelectActivity(rowData)}
                />
            ),
        },
        {
            title: t("activity.code"),
            field: "code",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: t("activity.name"),
            align: "left",
            field: "name"
        },
        {
            title: t("activity.description"),
            align: "left",
            field: "description",
        },
    ];

    async function handleChangSearchActivity({ keyword }) {
        const data = await getListActivityById(values?.project?.id, keyword);
        setListActivity(data);
    }

    const initialValues = { keyword: null };

    return (
        <GlobitsPopup
            open={open}
            noDialogContent
            scroll={"body"}
            title='Danh sách hoạt động dự án'
            size="md"
            onClosePopup={handleClose}
        >
            <Grid container className="p-8">
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={initialValues}
                        onSubmit={handleChangSearchActivity}
                    >
                        {({ resetForm, values, setFieldValue, setValues }) => {
                            return (
                                <Form autoComplete="off">
                                    <GlobitsTextField
                                        placeholder="Tìm kiếm hoạt động thuộc dự án..."
                                        name="keyword"
                                        variant="outlined"
                                        notDelay
                                        InputProps={{
                                            endAdornment: (
                                                <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                    <Search />
                                                </IconButton>
                                            ),
                                        }}
                                    />
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12} className="pt-8">
                    <MaterialTable
                        icons={{
                            Filter: React.forwardRef((props, ref) => <SearchIcon ref={ref} />),
                            Search: React.forwardRef((props, ref) => <SearchIcon ref={ref} />),
                            ResetSearch: React.forwardRef((props, ref) => <RotateLeftIcon ref={ref} />),
                            SortArrow: ArrowUpward,
                            DetailPanel: ChevronRight,
                        }}
                        columns={columns}
                        // data={getChildren(listActivity, "child")}
                        data={listActivity}
                        parentChildData={(row, rows) =>
                            rows.find((a) => a?.id === row?.parentId)
                        }
                        onRowClick={(evt, selectedRow) => handleSelectActivity(selectedRow)}
                        options={{
                            paging: false,
                            search: false,
                            toolbar: false,
                            showTitle: false,
                            defaultExpanded: true,
                            headerStyle: {
                                backgroundColor: "rgb(246, 246, 246)",
                                color: "rgb(113, 114, 118)",
                                fontSize: "15px",
                                // textAlign: "left",
                                padding: "8px 0px",
                                fontWeight: "bold",
                            },
                            actionsColumnIndex: -1,
                            rowStyle: (rowData, index) => ({
                                padding: "0px",
                                backgroundColor:
                                    (values?.activity && values?.activity?.id === rowData?.tableData?.id) ? "#EEE" : "#FFF",
                                fontWeight: rowData?.parent ? "normal" : "bold",
                            }),
                        }}
                        localization={{
                            body: {
                                emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
                            },
                            toolbar: {
                                searchPlaceholder: "Tìm kiếm",
                            },
                        }}
                    />
                </Grid>
            </Grid>


        </GlobitsPopup>
    );
}

export default memo(observer(ChoosePAPopup));