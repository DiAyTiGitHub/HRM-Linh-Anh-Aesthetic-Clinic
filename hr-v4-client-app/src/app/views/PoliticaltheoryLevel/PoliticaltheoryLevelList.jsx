import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {Button, ButtonGroup, Grid, Icon, IconButton} from "@material-ui/core";
import {observer} from "mobx-react";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import {Form, Formik} from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import PoliticaltheoryLevelForm from "./PoliticaltheoryLevelForm";

function MaterialButton(props) {
    const {item} = props;
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

export default observer(function PoliticalTheoryLevelList() {
    const {politicalTheoryLevelStore} = useStore();
    const {t} = useTranslation();

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const {
        politicalTheoryLevelList,
        totalPages,
        totalElements,
        setPageSize,
        search,
        searchObject,
        handleChangePage,
        handleDelete,
        handleDeleteList,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        handleSelectListPoliticalTheoryLevel,
        handleEditPoliticalTheoryLevel,
        handleSetSearchObject,
        selectedPoliticalTheoryLevelList
    } = politicalTheoryLevelStore;

    React.useEffect(() => {
        search();
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
                            handleEditPoliticalTheoryLevel(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert("Call Selected Here:" + rowData.id);
                        }
                    }}
                />
            ),
        },
        {title: t("politicaltheoryLevel.code"), minWidth: "100px", field: "code", align: "left", width: "150"},
        {title: t("politicaltheoryLevel.name"), minWidth: "200px", field: "name", width: "150"},
    ];

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await search();
    }

    return (
        <>
            <div className="content-index">
                <div className="index-breadcrumb">
                    <GlobitsBreadcrumb
                        routeSegments={[{name: t("politicaltheoryLevel.title")}]}
                    />
                </div>
                <Grid className="index-card" container spacing={2}>
                    <Grid item xs={12}>
                        <Formik
                            enableReinitialize
                            initialValues={{keyword: ""}}
                            onSubmit={handleFilter}
                        >
                            {({resetForm, values, setFieldValue, setValues}) => (
                                <Form autoComplete="off">
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} md={6}>
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<AddIcon/>}
                                                    type="button"
                                                    onClick={() => handleEditPoliticalTheoryLevel()}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                                <Button
                                                    disabled={selectedPoliticalTheoryLevelList?.length <= 0}
                                                    startIcon={<DeleteOutlineIcon/>}
                                                    onClick={handleDeleteList}
                                                >
                                                    {!isMobile && t("general.button.delete")}
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
                                                        startIcon={<SearchIcon/>}
                                                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                        type="submit"
                                                    >
                                                        Tìm kiếm
                                                    </Button>
                                                </ButtonGroup>
                                            </div>
                                        </Grid>
                                    </Grid>
                                </Form>
                            )}
                        </Formik>
                    </Grid>
                    <PoliticaltheoryLevelForm/>

                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationDialog}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationDeleteListDialog}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteList}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                    <Grid item xs={12}>
                        <GlobitsTable
                            selection
                            data={politicalTheoryLevelList}
                            handleSelectList={handleSelectListPoliticalTheoryLevel}
                            columns={columns}
                            totalPages={totalPages}
                            handleChangePage={handleChangePage}
                            setRowsPerPage={setPageSize}
                            pageSize={searchObject?.pageSize}
                            pageSizeOption={[10, 25, 50]}
                            totalElements={totalElements}
                            page={searchObject?.pageIndex}
                        />
                    </Grid>
                </Grid>
            </div>
        </>
    );
});