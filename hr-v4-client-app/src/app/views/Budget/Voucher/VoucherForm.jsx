import { Button,DialogActions, DialogContent, Grid, makeStyles } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import {Form, Formik} from "formik";
import { observer } from "mobx-react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import { useStore } from "../../../stores";
import { getBudgetById, pagingBudget } from "../Budget/BudgetService";

import VoucherRow from "./VoucherRow";
import VoucherItems from "./VoucherItems";
import localStorageService from "app/services/localStorageService";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
const defaultVoucher = {
  id: "",
  voucherDate: new Date().toISOString().slice(0, 16),
  totalAmount: 0,
  voucherCode: "",
  voucherType: 1, // Default to "thu" (1)
  voucherItems: [
    {
      amount: 0,
      budgetCategory: null,
      note: "",
    },
  ],
  budget: null, // Assuming you might need to select or associate with a Budget
};

const voucherTypeOptions = [
  { value: 1, name: "Thu" },
  { value: -1, name: "Chi" },
];
const useStyles = makeStyles(() => ({
  root: {
    background: "#E4f5fc",
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
    marginTop: "2px",
    overflowX: "auto",
    overflowY: "hidden",
    "& table": {
      border: "1px solid #ccc",
      borderCollapse: "collapse",
      "& td": {
        border: "1px solid #ccc",
      },
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #ccc",
    marginBottom: "8px",
    "& th": {
      width: "calc(100vw / 4)",
      border: "1px solid #ccc",
      padding: "8px 0 8px 4px",
    },
  },
}));

export default observer(function VoucherForm({ handleAfterSubmit, updateListOnClose, open }) {
  const { voucherStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, saveOrUpdateVoucher, selectedVoucher, voucherType } = voucherStore;

  const [voucher, setVoucher] = useState(defaultVoucher);

  const validationSchema = Yup.object({
    budget: Yup.object().nullable().required(t("validation.required")), // Make "Quỹ" required
  });

  useEffect(() => {
    if (selectedVoucher) setVoucher(selectedVoucher);
    else setVoucher(defaultVoucher);
  }, [selectedVoucher]);

  useEffect(() => {
    // Nếu không có voucherItems, đặt totalAmount bằng 0
    const totalAmount = voucher.voucherItems?.length ? voucher.voucherItems.reduce((acc, item) => acc + item.amount, 0) : 0;

    setVoucher((prevVoucher) => ({
      ...prevVoucher,
      totalAmount: totalAmount,
    }));
  }, [voucher.voucherItems]); // Trigger when voucherItems changes

  function handleFormSubmit(voucher) {
    let res = saveOrUpdateVoucher(voucher);
    if (handleAfterSubmit) handleAfterSubmit(res);
  }
  const classes = useStyles();
  useEffect(() => {
    function fetchBudgetData() {
      // Lấy quỹ mặc định từ localStorage
      const defaultBudget = localStorageService.getItem("defaultBudget");
  
      if (selectedVoucher) {
        setVoucher(selectedVoucher);
      } else {
        let validBudget = null;
  
        if (defaultBudget) {
          // Gọi API hoặc kiểm tra quỹ hợp lệ
          getBudgetById(defaultBudget.id)
            .then((budgetData) => {
              if (budgetData) {
                validBudget = budgetData.data; // Nếu quỹ tồn tại
              } else {
                localStorageService.removeItem("defaultBudget"); // Xóa khỏi localStorage nếu quỹ không tồn tại
              }
            })
            .catch((error) => {
              console.error("Budget not found or error occurred:", error);
              localStorageService.removeItem("defaultBudget"); // Xóa nếu gặp lỗi
            })
            .finally(() => {
              // Cập nhật voucher sau khi xử lý
              setVoucher((prevVoucher) => ({
                ...defaultVoucher,
                budget: validBudget, // Đặt quỹ hợp lệ (hoặc null nếu không tồn tại)
                voucherType: voucherType || 1, // Lấy từ store hoặc mặc định là 1
              }));
            });
        } else {
          // Không có quỹ mặc định, thiết lập voucher ngay
          setVoucher((prevVoucher) => ({
            ...defaultVoucher,
            budget: validBudget, // null vì không có quỹ mặc định
            voucherType: voucherType || 1, // Lấy từ store hoặc mặc định là 1
          }));
        }
      }
    }
  
    fetchBudgetData();
  }, [selectedVoucher, voucherType]);
  
  

  function handleBudgetChange(selectedBudget) {
    // Lưu quỹ được chọn vào localStorage
    localStorage.setItem("defaultBudget", JSON.stringify(selectedBudget));
    setVoucher((prevVoucher) => ({
      ...prevVoucher,
      budget: selectedBudget,
    }));
  }

  return (
    <GlobitsPopupV2 open={open} noDialogContent title={(selectedVoucher?.id ? t("general.button.edit") : t("general.button.add")) + " hóa đơn"} onClosePopup={handleClose}>
      <Formik validationSchema={validationSchema} enableReinitialize initialValues={voucher} onSubmit={(values) => handleFormSubmit(values)}>
        {({ isSubmitting, values }) => (
          <Form autoComplete='off'>
            <div className='dialog-body'>
              <DialogContent className='o-hidden'>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <GlobitsDateTimePicker label={t("voucher.voucherDate")} name='voucherDate' isDateTimePicker />
                  </Grid>

                  <Grid item xs={6}>
                    <GlobitsTextField label={t("voucher.voucherCode")} name='voucherCode' />
                  </Grid>

                  <Grid item xs={6}>
                    <GlobitsSelectInput label={t("voucher.voucherType")} name='voucherType' keyValue='value' options={voucherTypeOptions} />
                  </Grid>

                  <Grid item xs={12} sm={12} md={6} xl={6}>
                    <GlobitsPagingAutocomplete
                      label={t("budget.title")}
                      name={"budget"}
                      api={pagingBudget}
                      required
                      onChange={(e, selectedBudget) => handleBudgetChange(selectedBudget)} // Gọi hàm khi thay đổi
                    />
                  </Grid>

                  <VoucherItems />
                  {/* <FieldArray name='voucherItems'>
                    {({ insert, remove, push }) => (
                      <>
                        <Grid item xs={12}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={4} md={3} lg={2}>
                              <ButtonGroup color='container' aria-label='outlined primary button group'>
                                <Button
                                  startIcon={<AddIcon />}
                                  type='button'
                                  onClick={() => {
                                    push({
                                      voucherItems: null,
                                    });
                                  }}>
                                  Thêm khoản
                                </Button>
                              </ButtonGroup>
                            </Grid>
                          </Grid>
                        </Grid>

                        <Grid item xs={12} style={{ overflowX: "auto" }}>
                          <section className={classes.tableContainer}>
                            <table className={classes.table}>
                              <thead>
                                <tr className={classes.tableHeader}>
                                  <th width='10%'>Thao tác</th>
                                  <th width='20%'>{t("voucherItem.amount")} </th>
                                  <th width='30%'>{t("voucherItem.name")}</th>
                                  <th width='30%'>{t("voucherItem.note")}</th>
                                </tr>
                              </thead>
                              <tbody>
                                {values?.voucherItems?.length > 0 ? (
                                  values?.voucherItems?.map((order, index) => (
                                    <VoucherRow
                                      key={index}
                                      index={index}
                                      voucherItems={values?.voucherItems}
                                      nameSpace={`voucherItems[${index}]`}
                                      remove={() => remove(index)}
                                      push={() => push(index)}
                                      //  disabled={!hasEditPermission}
                                    />
                                  ))
                                ) : (
                                  <tr className='row-table-body row-table-no_data'>
                                    <td colSpan={5} align='center' className='py-8'>
                                      Chưa có thông tin
                                    </td>
                                  </tr>
                                )}
                              </tbody>
                            </table>
                          </section>
                        </Grid>
                      </>
                    )}
                  </FieldArray> */}
                  {/* <Grid item xs={12}>
                    <ListVoucherItem voucherItems={voucher.voucherItems} setVoucherItems={(updatedItems) => setVoucher({ ...voucher, voucherItems: updatedItems })} />
                  </Grid> */}
                </Grid>
              </DialogContent>
            </div>
            <div className='dialog-footer'>
              <DialogActions className='p-0'>
                <div className='flex flex-space-between flex-middle'>
                  <Button startIcon={<BlockIcon />} variant='contained' className='mr-12 btn btn-secondary d-inline-flex' color='secondary' onClick={() => handleClose()}>
                    {t("general.button.cancel")}
                  </Button>
                  <Button startIcon={<SaveIcon />} className='mr-0 btn btn-primary d-inline-flex' variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
            </div>
          </Form>
        )}
      </Formik>
    </GlobitsPopupV2>
  );
});
