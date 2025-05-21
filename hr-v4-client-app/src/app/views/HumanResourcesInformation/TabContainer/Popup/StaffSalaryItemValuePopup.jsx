import React, { memo, useEffect, useState } from "react";
import { Grid, DialogActions, Button, DialogContent, makeStyles, TableCell, Table, TableHead, TableRow, TableBody, } from "@material-ui/core";
import { Formik, Form, useFormikContext, FieldArray, getIn } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingSalaryTemplates } from "app/views/Salary/SalaryTemplate/SalaryTemplateService";
import { pagingStaff } from "../../StaffService";
import { useStore } from "app/stores";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import LocalConstants from "app/LocalConstants";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: "10px 15px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
  tableContainer: {
    marginTop: "16px",
    overflowX: "auto",
    overflowY: "auto",
    maxHeight: "60vh",
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      textAlign: "center",
    },
    "& .MuiTableCell-head": {
      padding: "10px",
      minWidth: "150px",
      border: "1px solid #e9ecef",
      textAlign: "center",
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #E3F2FD",
    marginBottom: "8px",
    "& th": {
      width: "calc(100vw / 4)",
    },
  },
}));

function StaffSalaryItemValuePopup(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;

  const initialItem = {
    staff: null,
    salaryTemplate: null
  };

  const validationSchema = Yup.object({

  });
  const [formValues, setFormValues] = useState(null);
  const { staffSalaryItemValueStore } = useStore();
  const [data, setData] = useState([]);
  const { staff, salaryTemplate } = props;
  const { getListSalaryTemplateItem } = staffSalaryItemValueStore;

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
      const fetchData = async () => {
        if (item?.salaryTemplate && item?.staff) {
          try {
            const response = await getListSalaryTemplateItem({
              salaryTemplate: item?.salaryTemplate,
              staff: item?.staff,
            });
            if (response) {
              setData(response);
              setFormValues({
                staff: item?.staff,
                salaryTemplate: {
                  ...item?.salaryTemplate,
                  templateItems: response
                }
              });
            }
          } catch (error) {
            console.error("Lỗi khi lấy dữ liệu:", error);
            setData([]);
          }
        } else {
          setData([]);
        }
      };
      fetchData();
    } else {
      setFormValues({ ...initialItem });
    }

  }, [item]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      // size="xl"
      noDialogContent
      open={open}
      onClosePopup={handleClose}
      title={
        <span className="">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("Lương cố định")}
        </span>
      }
    >
      <Formik
        initialValues={formValues}
        onSubmit={(values) => handleSubmit(values)}
        validationSchema={validationSchema}
      >
        {({ isSubmitting, values, setFieldValue, setFieldTouched, setFieldError }) => {
          return (
            <Form autoComplete="off">
              <DialogContent
                className="dialog-body p-12"
              >
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <GlobitsPagingAutocomplete
                      label="Chọn nhân viên"
                      name={"staff"}
                      // displayData={"displayName"}
                      api={pagingStaff}
                      required
                      disabled

                      getOptionLabel={(option) => {
                          return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                      }}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsPagingAutocomplete
                      label="Chọn mẫu bảng lương"
                      name={"salaryTemplate"}
                      api={pagingSalaryTemplates}
                      displayData={"name"}
                      required
                      disabled
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <StaffSalaryItemValueListSection
                      staff={values?.staff}
                      salaryTemplate={values?.salaryTemplate}
                    />
                  </Grid>
                </Grid>
              </DialogContent>

              <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={handleClose}
                  >
                    {t("general.button.close")}
                  </Button>
                  <Button
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
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
}


export default memo(observer(StaffSalaryItemValuePopup));

const StaffSalaryItemValueListSection = (props) => {
  const { t } = useTranslation();

  const classes = useStyles();

  const {
    values,
    setFieldValue
  } = useFormikContext();

  return (
    <FieldArray
      name="salaryTemplate.templateItems"
      render={(arrayHelpers) => (
        <div className={classes.groupContainer}>
          <div className={classes.tableContainer}>
            {getIn(values, "salaryTemplate.templateItems")?.length > 0 ? (
              <Table style={{ tableLayout: "auto" }}>
                <TableHead>
                  <TableRow>
                    <TableCell colSpan={1}>
                      {t("Thành phần lương")}
                    </TableCell>
                    <TableCell colSpan={1}>
                      {t("Giá trị cố định")}
                    </TableCell>
                    <TableCell colSpan={1}>
                      {t("Loại giá trị")}
                    </TableCell>

                  </TableRow>
                </TableHead>
                <TableBody>
                  {getIn(values, "salaryTemplate.templateItems")?.map((item, index) => (
                    <TableRow key={index}>
                      <TableCell colSpan={1}>
                        {item?.displayName
                          ? item?.displayName
                          : null}
                      </TableCell>
                      <TableCell colSpan={1}>
                        <GlobitsVNDCurrencyInput
                          name={`salaryTemplate.templateItems.${index}.value`}
                        />
                      </TableCell>
                      <TableCell colSpan={1}>
                        {LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == item?.salaryItem?.calculationType)?.name}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <p className="flex justify-center">

              </p>
            )}
          </div>
        </div>
      )}
    />
  )
}

