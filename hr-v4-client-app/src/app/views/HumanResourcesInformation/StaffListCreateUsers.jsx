import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { formatDate } from "app/LocalFunction";
import GlobitsTable from "app/common/GlobitsTable";

function StaffListCreateUsers() {
  const { t } = useTranslation();
  const { staffStore } = useStore();

  let columns = [
    {
      title: "Mã nhân viên",
      field: "staffCode",
      align: "center",
      render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
    },
    {
      title: "Nhân viên",
      minWidth: "200px",
      render: (rowData) => (
        <>
          {rowData.displayName && (
            <p className='m-0'>
              <strong>{rowData.displayName}</strong>
            </p>
          )}

          {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

          {rowData.gender && <p className='m-0'>Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

          {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
        </>
      ),
    },
    {
      title: "Thông tin liên hệ",
      field: "info",
      minWidth: "200px",
      render: (rowData) => (
        <>
          {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

          {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
        </>
      ),
    },

    {
      title: "Phòng ban",
      field: "department.name",
      align: "left",
      minWidth: "120px",
      render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
    },

    {
      title: "Nơi ở hiện tại",
      field: "currentResidence",
      align: "left",
      minWidth: "180px",
      render: (rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
    },

    // {
    //   title: "Loại hợp đồng",
    //   field: "labourAgreementType.name",
    //   align: "left",
    //   minWidth: "150px",
    // },
  ];
  const { handleClose, recentSWH, selectedStaff, shouldOpenConfirmationCreateUsers, listCreateAbleUsers, handleConfirmCreateUse } = staffStore;
  // const { handleClose, saveStaffWorkingHistory, pagingStaffWorkingHistory, selectedStaffWorkingHistory, openCreateEditPopup, isAdmin } = staffWorkingHistoryStore;

  const validationSchema = Yup.object({});

  async function handleSaveForm(values) {
    handleConfirmCreateUse();
    // handleClose();
  }

  return (
    <GlobitsPopupV2
      size='md'
      scroll={"body"}
      open={
        // true
        shouldOpenConfirmationCreateUsers && listCreateAbleUsers?.length !== 0
      }
      noDialogContent
      title={t(`Xác nhận tạo User cho ${listCreateAbleUsers?.length} nhân viên `)}
      onClosePopup={handleClose}>
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={{
          staff: selectedStaff,
          toOrganization: null,
          toDepartment: null,
          toPosition: null,
        }}
        onSubmit={handleSaveForm}>
        {({ isSubmitting, values, setFieldValue, initialValues, errors, touched }) => {
          return (
            <Form autoComplete='off'>
              <DialogContent className='dialog-body p-12'>
                <Grid container spacing={2}>
                  <GlobitsTable nonePagination columns={columns} data={listCreateAbleUsers || []} />
                </Grid>
              </DialogContent>

              <DialogActions className='dialog-footer px-12'>
                <div className='flex flex-space-between flex-middle'>
                  <Button variant='contained' className='mr-12 btn btn-secondary d-inline-flex' color='secondary' disabled={isSubmitting} onClick={handleClose}>
                    {t("general.button.close")}
                  </Button>
                  <Button className='mr-0 btn btn-primary d-inline-flex' variant='contained' color='primary' type='submit' disabled={isSubmitting || listCreateAbleUsers?.length === 0}>
                    {t("general.button.createUser")}
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

export default memo(observer(StaffListCreateUsers));
