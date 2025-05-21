import { DialogContent, Grid, IconButton } from "@material-ui/core";
import { Search } from "@material-ui/icons";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import SelectOrganizationList from "./SelectOrganizationList";

function SelectOrganizationPopup({ open, handleSelect, handleClose }) {
    const { t } = useTranslation();
    const { organizationStore } = useStore();
    const { searchObject, handleSetSearchObject, pagingOrganization, resetStore } = organizationStore;

    React.useEffect(() => {
        pagingOrganization();

        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingOrganization();
    }

    return (
        <GlobitsPopupV2
            popupId='select-org-popup'
            scroll={"body"}
            size='lg'
            open={open}
            noDialogContent
            title={t("navigation.organization.select")}
            onClosePopup={handleClose}>
            <DialogContent className='p-12' style={{ overflowY: "auto", maxHeight: "75vh" }}>
                <Grid container spacing={2}>
                    <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                    <Grid item lg={6} md={6} sm={8} xs={8}>
                        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
                            {({ resetForm, values, setFieldValue, setValues }) => {
                                return (
                                    <Form autoComplete='off'>
                                        <GlobitsTextField
                                            placeholder='Tìm kiếm theo mã, tên công ty, tổ chức...'
                                            name='keyword'
                                            variant='outlined'
                                            notDelay
                                            InputProps={{
                                                endAdornment: (
                                                    <IconButton className='py-0 px-4' aria-label='search' type='submit'>
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

                    <Grid item xs={12}>
                        <SelectOrganizationList handleSelect={handleSelect} />
                    </Grid>
                </Grid>
            </DialogContent>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SelectOrganizationPopup));
