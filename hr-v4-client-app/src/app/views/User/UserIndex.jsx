import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, Popover } from "@material-ui/core";
import PublishIcon from "@material-ui/icons/Publish";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import UserList from "./UserList";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import UserForm from "./UserForm";
import UserToolbar from "./UserToolbar";

export default observer (function UserIndex () {
  const {userStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();
  const [anchorEl, setAnchorEl] = React.useState (null);

  const open = Boolean (anchorEl);
  const id = open? "simple-popper" : undefined;

  const {
    updatePageData,
    shouldOpenEditorDialog,
    shouldOpenConfirmationDialog,
    handleClose,
    handleConfirmDelete,
    openViewPopup
  } = userStore;

  useEffect (() => {
    updatePageData ();
  }, [updatePageData]);

  const handleClick = (event) => {
    setAnchorEl (anchorEl? null : event.currentTarget);
  };

  const {checkAllUserRoles} = hrRoleUtilsStore;
  useEffect (() => {
    checkAllUserRoles ()
  }, []);

  return (
      <div className='content-index'>
        <div className='index-breadcrumb py-6'>
          <GlobitsBreadcrumb routeSegments={
            [
              {name:t ("navigation.administration.title")},
              {name:t ("navigation.administration.accounts")}
            ]
          }/>
        </div>
        <Grid className='index-card' container spacing={2}>
          <Grid item xs={12}>
            <UserToolbar/>
          </Grid>

          {shouldOpenEditorDialog && <UserForm/>}
          {openViewPopup && <UserForm readOnly={true}/>}

          {shouldOpenConfirmationDialog && (
              <GlobitsConfirmationDialog
                  open={shouldOpenConfirmationDialog}
                  onConfirmDialogClose={handleClose}
                  onYesClick={handleConfirmDelete}
                  title={t ("confirm_dialog.delete.title")}
                  text={t ("confirm_dialog.delete.text")}
                  agree={t ("confirm_dialog.delete.agree")}
                  cancel={t ("confirm_dialog.delete.cancel")}
              />
          )}

          <Grid item xs={12}>
            <UserList/>
          </Grid>

          <Popover
              id={id}
              open={open}
              anchorEl={anchorEl}
              onClose={handleClick}
              anchorOrigin={{
                vertical:"bottom",
                horizontal:"right",
              }}
              transformOrigin={{
                vertical:"top",
                horizontal:"right",
              }}>
            <div className='menu-list-button'>
              <div className='menu-item-button'>
                <PublishIcon style={{fontSize:16, transform:"rotate(180deg)"}}/> Kết xuất danh sách
              </div>
              <div className='menu-item-button'>
                <PublishIcon style={{fontSize:16}}/> Import dữ liệu
              </div>
            </div>
          </Popover>
        </Grid>
      </div>
  );
});
