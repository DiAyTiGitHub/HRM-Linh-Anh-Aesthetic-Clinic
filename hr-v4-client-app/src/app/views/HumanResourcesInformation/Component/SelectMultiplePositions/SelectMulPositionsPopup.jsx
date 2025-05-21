import React, { memo, useEffect } from "react";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectMulPositionsList from "./SelectMulPositionsList";
import SelectMulPositionToolbar from "./SelectMulPositionToolbar";
import { Form, Formik } from "formik";
import { pagingPosition as pagingPositionApi } from 'app/views/Position/PositionService';
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";

function SelectMulPositionsPopup (props) {
  const {t} = useTranslation ();

  const {isDisabled, currentPositions, handleSubmit, searchObject} = props;

  const {positionStore} = useStore ();

  const {
    pagingPosition,
    openSelectMultiplePopup,
    handleClose,
    resetStore,
    handleOpenConfirmAssignPopup,
    handleSelectListDelete,
    handleMergeSearchObject,
  } = positionStore;

  useEffect (() => {
    if (searchObject) {
      handleMergeSearchObject (searchObject);
    }
    pagingPosition ();

    return resetStore;
  }, []);
  const handleSaveForm = (values) => {
    handleSelectListDelete (values?.positions)
    handleOpenConfirmAssignPopup ();
  }
  return (
      <GlobitsPopupV2
          scroll={"body"}
          size='md'
          open={openSelectMultiplePopup}
          noDialogContent
          title={"Lựa chọn các vị trí của nhân viên"}
          onClosePopup={handleClose}>
        <Formik
            enableReinitialize
            initialValues={{positions:[]}}
            onSubmit={handleSaveForm}>
          {({isSubmitting, values, setFieldValue, initialValues}) => {

            return (
                <Form autoComplete='off' autocomplete='off'>
                  <div className='dialog-body'>
                    <DialogContent className='p-12'>
                      <Grid container spacing={2}>
                        <Grid item xs={12}>
                          <SelectMulPositionToolbar/>
                        </Grid>
                        <Grid item xs={12}>
                          <GlobitsPagingAutocompleteV2
                              label={t ("Các vị trí đã lựa chọn")}
                              name="positions"
                              multiple
                              api={pagingPositionApi}
                              getOptionLabel={(option) =>
                                  [option?.name, option?.code].filter (Boolean).join (' - ') || ''
                              }
                          />
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
                            type={"submit"}
                        >
                          Xác nhận
                        </Button>
                      </div>
                    </DialogActions>
                  </div>
                </Form>
            );
          }}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (SelectMulPositionsPopup));
