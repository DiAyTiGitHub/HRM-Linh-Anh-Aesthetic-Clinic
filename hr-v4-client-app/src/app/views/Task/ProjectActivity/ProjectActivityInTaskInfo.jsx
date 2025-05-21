import React, {memo, useEffect, useState} from "react";
import {Grid, IconButton, Radio,} from "@material-ui/core";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import {useTranslation} from "react-i18next";
import {Form, Formik, useFormikContext} from "formik";
import Search from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";

function ProjectActivityInTaskInfo({
                                       open,
                                       setIsOpenListActivity,
                                       name = 'activity',
                                       nameProject = 'project',
                                       handleClose
                                   }) {
    const [listActivity, setListActivity] = useState([]);

    const {t} = useTranslation();

    const {projectStore} = useStore();
    const {getListActivityById} = projectStore;

    const {values, setFieldValue} = useFormikContext();


    useEffect(() => {
        if (values[nameProject]) {
            getListActivityById(values[nameProject]?.id).then((data) => {
                setListActivity(data);
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [getListActivityById, values[nameProject]]);

    const handleSelectActivity = (activity) => {
        if (values[name] && values[name]?.id === activity?.id) {
            setFieldValue(name, null);
        } else {
            setFieldValue(name, activity);
            handleClose();
        }
    };

    const columns = [{
        title: t("general.popup.select"), align: "center", cellStyle: {
            textAlign: "center", display: "flex", justifyContent: "center"
        }, render: (rowData) => (<Radio
                className="pr-8"
                id={`radio${rowData?.id}`}
                name="radSelected"
                value={rowData.id}
                checked={values[name] ? values[name]?.id === rowData?.id : false}
                onClick={(event) => handleSelectActivity(rowData)}
            />),
    }, {
        title: t("activity.code"), field: "code", align: "left", cellStyle: {
            textAlign: "left",
        },
    }, {
        title: t("activity.name"), align: "left", field: "name"
    }, {
        title: t("activity.description"), align: "left", field: "description",
    },];

    async function handleChangSearchActivity({keyword}) {
        const data = await getListActivityById(values[nameProject]?.id, keyword);
        setListActivity(data);
    }

    const initialValues = {keyword: null};

    return (<GlobitsPopupV2
            open={open}
            noDialogContent
            scroll={"body"}
            title='Danh sách hoạt động thuộc dự án'
            size="md"
            onClosePopup={() => setIsOpenListActivity(false)}
        >
            <Grid container className="p-8">
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={initialValues}
                        onSubmit={handleChangSearchActivity}
                    >
                        {({resetForm, values, setFieldValue, setValues}) => {
                            return (<Form autoComplete="off">
                                    <GlobitsTextField
                                        placeholder="Tìm kiếm hoạt động thuộc dự án..."
                                        name="keyword"
                                        variant="outlined"
                                        notDelay
                                        InputProps={{
                                            endAdornment: (
                                                <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                    <Search/>
                                                </IconButton>),
                                        }}
                                    />
                                </Form>);
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12} className="pt-8">
                    <GlobitsTable
                        data={listActivity}
                        columns={columns}
                        nonePagination
                        colParent
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>);
}

export default memo(observer(ProjectActivityInTaskInfo));