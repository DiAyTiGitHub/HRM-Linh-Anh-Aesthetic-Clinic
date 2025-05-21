import React, { useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectMulPositionsList from "./SelectMulPositionsList";
import SelectMulPositionToolbar from "./SelectMulPositionToolbar";
import PositionCUForm from "../PositionCUForm";

function SelectMulPositionsPopup (props) {
  const {t} = useTranslation ();

  const {isDisabled, currentPositions, handleSubmit, searchObject} = props;

  const {positionStore} = useStore ();

  const {
    pagingPosition,
    openSelectMultiplePopup,
    handleClose,
    resetStore,
    handleCloseConfirmAssignPopup,
    handleOpenConfirmAssignPopup,
    openCreateEditPopup,
    listOnDelete,
    handleMergeSearchObject,
  } = positionStore;

  useEffect (() => {
    if (searchObject) {
      handleMergeSearchObject (searchObject);
    }
    pagingPosition ();

    return resetStore;
  }, []);

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size='md'
          open={openSelectMultiplePopup}
          noDialogContent
          title={"Lựa chọn các vị trí của nhân viên"}
          onClosePopup={handleClose}>
        <>
          <div className='dialog-body'>
            <DialogContent className='p-12'>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <SelectMulPositionToolbar/>
                </Grid>
                <Grid item xs={12}>
                  <SelectMulPositionsList/>
                </Grid>
              </Grid>
            </DialogContent>
          </div>

          <div className='dialog-footer dialog-footer-v2 py-8'>
            <DialogActions className='p-0'>
              <div className='flex flex-space-between flex-middle'>
                <Button
                    startIcon={<BlockIcon/>}
                    variant='contained'
                    className='mr-12 btn btn-secondary d-inline-flex'
                    color='secondary'
                    onClick={handleClose}>
                  {t ("general.button.cancel")}
                </Button>
                <Button
                    startIcon={<SaveIcon/>}
                    className='mr-0 btn bgc-lighter-dark-blue d-inline-flex'
                    variant='contained'
                    color='primary'
                    onClick={() => {
                      if (handleSubmit) {
                        handleSubmit (listOnDelete);
                      } else {
                        handleOpenConfirmAssignPopup (listOnDelete);
                      }
                    }}>
                  Xác nhận
                </Button>
              </div>
            </DialogActions>
          </div>
        </>
      </GlobitsPopupV2>
  );
}

export default memo (observer (SelectMulPositionsPopup));
