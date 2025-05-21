import React from 'react';
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import { useTranslation } from 'react-i18next';
import GlobitsTable from 'app/common/GlobitsTableNotPagination';
import GlobitsBreadcrumb from 'app/common/GlobitsBreadcrumb';
import { Grid, Button } from "@material-ui/core";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import AddIcon from "@material-ui/icons/Add";
import { useStore } from '../../stores';
import GlobitsConfirmationDialog from 'app/common/GlobitsConfirmationDialog';
import GlobalPropertyForm from './GlobalPropertyForm';
import { useEffect } from 'react';

function MaterialButton(props) {
    const { item } = props;

    return (
        <div>
            <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function GlobalPropertyIndex() {
    const { t } = useTranslation();

    const theme = useTheme();
    const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

    const { globalPropertyStore } = useStore();
    const {
        globalPropertyList,
        handleOpenPopupForm,
        handleClosePopup,
        handleConfirmDelete,
        handleOpenPopupConfirmDelete,
        loadAllGlobalProperty,
        openPopupConfirmDelete
    } = globalPropertyStore;

    useEffect(() => {
        loadAllGlobalProperty()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleOpenPopupForm(rowData.property);
                        } else if (method === 1) {
                            handleOpenPopupConfirmDelete(rowData);
                        } else {
                            alert("Call Selected Here:" + rowData.id);
                        }
                    }}
                />
            ),
        },
        { title: 'Property', field: "property", align: "left" },
        { title: 'Tên Property', field: "propertyName" },
        { title: 'Value Property', field: "propertyValue" },
        { title: 'Tên Data Type', field: "dataTypeName" },
        { title: 'Mô tả', field: "description" },

    ];

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb routeSegments={[{ name: "Global Property" }]} />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item lg={1} md={2} sm={6} xs={6}>
                    <Button
                        className={`${isExtraSmall ? '' : 'mb-16 mr-16 '} btn btn-info d-inline-flex`}
                        startIcon={<AddIcon />}
                        variant="contained"
                        onClick={() => {
                            handleOpenPopupForm();
                        }}
                        fullWidth
                    >
                        {t("general.button.add")}
                    </Button>
                </Grid>

                <GlobalPropertyForm />

                <GlobitsConfirmationDialog
                    open={openPopupConfirmDelete}
                    onConfirmDialogClose={handleClosePopup}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
                <Grid item xs={12}>
                    <GlobitsTable
                        data={globalPropertyList}
                        columns={columns}
                    />
                </Grid>
            </Grid>
        </div>
    )
})