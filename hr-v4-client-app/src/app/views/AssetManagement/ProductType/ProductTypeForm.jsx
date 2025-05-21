import React, {memo, useEffect, useState} from 'react'
import { useStore } from 'app/stores';
import { Form, Formik } from 'formik';
import * as Yup from "yup";
import { useTranslation } from 'react-i18next';
import { DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import { observer } from 'mobx-react';
import GlobitsPopupV2 from 'app/common/GlobitsPopupV2';
import {CodePrefixes} from "../../../LocalConstants";

const ProductTypeForm = () => {
  const { t } = useTranslation();

  const { selectedProductType, onSaveProductType, onClosePopup,autoGenCode } = useStore().assetManagementStore.productTypeStore;
  const [productTypeForm , setProductTypeForm] = useState(selectedProductType);

  const autoGenCodeFunc = async () => {
    const code = await autoGenCode(CodePrefixes.LOAI_CONG_CU_DUNG_CU);

    if (code) {
      // Tạo object mới để tránh thay đổi trực tiếp state
      const updated = {...selectedProductType, ...{code:code}};
      setProductTypeForm(updated);
    }
  };
  useEffect(() => {
    if(!selectedProductType?.id){
      autoGenCodeFunc();
    }
  }, []);
  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.required")).nullable(),
    name: Yup.string().required(t("validation.required")).nullable(),
  });

  return (
    <GlobitsPopupV2
      open={Boolean(selectedProductType)}
      noDialogContent
      title="Loạicông cụ/ dụng cụ"
      onClosePopup={onClosePopup}
      size="sm"
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={productTypeForm}
        onSubmit={onSaveProductType}
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <DialogContent className="o-hidden dialog-body">
              <GlobitsTextField validate label="Tên" name="name" className="mt-2" />

              <div className='mt-8'>
                <GlobitsTextField validate label="Mã" name="code" />
              </div>

              <div className='mt-8'>
                <GlobitsTextField label="Mô tả" name="description" multiline rows={3} />
              </div>
            </DialogContent>

            <DialogActions className="p-0 dialog-footer">
              <div className="flex flex-space-between flex-middle">
                <Button
                  startIcon={<BlockIcon />}
                  variant="contained"
                  className="mr-12 btn btn-secondary d-inline-flex"
                  color="secondary"
                  onClick={() => onClosePopup()}
                >
                  {t("general.button.cancel")}
                </Button>
                <Button
                  startIcon={<SaveIcon />}
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </Form>
        )}
      </Formik>
    </GlobitsPopupV2>
  )
}

export default memo(observer(ProductTypeForm))