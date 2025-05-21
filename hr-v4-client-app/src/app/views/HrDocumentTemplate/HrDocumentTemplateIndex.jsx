/* eslint-disable react-hooks/exhaustive-deps */
import { Button, ButtonGroup, Grid } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import HrDocumentTemplateList from "./HrDocumentTemplateList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import HrDocumentTemplateForm from "./HrDocumentTemplateForm";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

export default observer(function HrDocumentTemplateIndex() {
    const { hrDocumentTemplateStore } = useStore();
    const { t } = useTranslation();

    const {
        paging,
        handleSetSearchObject,
        handleEditHrDocumentTemplate,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        resetHrDocumentTemplateStore
    } = hrDocumentTemplateStore;

    useEffect(() => {
        paging();
        return resetHrDocumentTemplateStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values, pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await paging()
    }

    return (<div className='content-index'>
        <div className='index-breadcrumb'>
            <GlobitsBreadcrumb routeSegments={[{ name: "Nhân viên" }, { name: "Mẫu tài liệu nhân viên" }]}
            />
        </div>
        <Grid className='index-card' container spacing={3}>
            <Grid item xs={12}>
                <Formik
                    enableReinitialize
                    initialValues={{ keyword: "" }}
                    onSubmit={handleFilter}
                >
                    {({ resetForm, values, setFieldValue, setValues }) => (<Form autoComplete="off">
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={6}>
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        startIcon={<AddIcon />}
                                        type="button"
                                        onClick={() => handleEditHrDocumentTemplate()}
                                    >
                                        Thêm mới
                                    </Button>
                                </ButtonGroup>
                            </Grid>
                            <Grid item xs={12} md={6}>
                                <div className="flex justify-between align-center">
                                    <GlobitsTextField
                                        placeholder="Tìm kiếm theo từ khóa"
                                        name="keyword"
                                        variant="outlined"
                                        notDelay
                                    />
                                    <ButtonGroup
                                        className="filterButtonV4"
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<SearchIcon />}
                                            className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                            type="submit"
                                        >
                                            Tìm kiếm
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </Grid>
                        </Grid>
                    </Form>)}
                </Formik>
            </Grid>
            <Grid item xs={12}>
                <HrDocumentTemplateList />
            </Grid>
        </Grid>

        {shouldOpenEditorDialog && <HrDocumentTemplateForm open={shouldOpenEditorDialog} />}

        <GlobitsConfirmationDialog
            open={shouldOpenConfirmationDialog}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
        />
    </div>);
});
