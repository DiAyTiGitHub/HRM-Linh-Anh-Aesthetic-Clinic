import {observer} from "mobx-react";
import React, {memo, useEffect, useState} from "react";
import {useStore} from "../../stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import ProjectList from "./ProjectList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import {useHistory} from "react-router-dom";
import localStorageService from "app/services/localStorageService";
import ProjectActivityInProjectToolbar from "./ProjectToolbar";


function ProjectIndex() {
    const {projectStore} = useStore();
    const {t} = useTranslation();
    const history = useHistory();

    const {
        updatePageData,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedProjectList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog,
        shouldOpenConfirmationDeleteActivityDialog,
        handleConfirmDeleteList,
        handleConfirmDeleteActivity,
        canCreateUpdateDeleteProject
    } = projectStore;

    useEffect(() => {
        updatePageData({keyword: "", pageIndex: 1, pageSize: 10});
    }, [updatePageData]);
    const [isTester, setTester] = useState(false);

    useEffect(() => {
        let roles = localStorageService
            .getLoginUser()
            ?.user?.roles?.map((item) => item.authority) || [];

        let auth = ["HR_TESTER"];

        if (roles.some((role) => auth.indexOf(role) !== -1)) {
            setTester(true);
        } else {
            setTester(false);
        }
    }, []);

    return (<div className="content-index">
        <div className="index-breadcrumb">
            <GlobitsBreadcrumb routeSegments={[{name: "Công việc"}, {name: t("project.title")}]}/>
        </div>
        <Grid className="index-card" container spacing={3}>
            {/* <Grid item lg={6} md={6} sm={4} xs={4}>
          {!isTester && canCreateUpdateDeleteProject() && <Button
            className="mr-16 btn btn-info d-inline-flex"
            startIcon={<AddIcon />}
            variant="contained"
            onClick={() => history.push(ConstantList.ROOT_PATH + `timesheet/project/create`)}
          >
            {t("general.button.add")}
          </Button>}

          {selectedProjectList?.length > 0 && canCreateUpdateDeleteProject() && (
            <Button
              className="mr-36 btn btn-warning d-inline-flex"
              variant="contained"
              startIcon={<DeleteIcon />}
              onClick={handleDeleteList}
            >
              {t("general.button.delete")}
            </Button>
          )}
        </Grid>
        <Grid item lg={6} md={6} sm={8} xs={8}>
          <GlobitsSearchInput search={updatePageData} t={t} />
        </Grid> */}

            {/*<Grid item xs={12} className="pb-0">*/}
            {/*  <ProjectActivityFilter />*/}
            {/*</Grid>*/}
            <Grid item xs={12}>
                <ProjectActivityInProjectToolbar/>
            </Grid>
            <ProjectList isTester={isTester}/>

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
                open={shouldOpenConfirmationDeleteActivityDialog}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteActivity}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDeleteListDialog}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteList}
                title={t("confirm_dialog.delete_list.title")}
                text={t("confirm_dialog.delete_list.text")}
                agree={t("confirm_dialog.delete_list.agree")}
                cancel={t("confirm_dialog.delete_list.cancel")}
            />
        </Grid>
    </div>);
}

export default memo(observer(ProjectIndex));
