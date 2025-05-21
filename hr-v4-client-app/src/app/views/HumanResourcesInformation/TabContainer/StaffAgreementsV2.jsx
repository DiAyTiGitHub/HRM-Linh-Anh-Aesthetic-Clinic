import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import StaffLabourAgreementCUForm from "app/views/StaffLabourAgreement/StaffLabourAgreementCUForm";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import "react-toastify/dist/ReactToastify.css";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiAccordion-rounded": { borderRadius: "5px" },
    "& .MuiPaper-root": { borderRadius: "5px" },
    "& .MuiAccordionSummary-root": {
      borderRadius: "5px",
      color: "#5899d1",
      fontWeight: "400",
      "& .MuiTypography-root": { fontSize: "1rem" },
    },
    "& .Mui-expanded": {
      "& .MuiAccordionSummary-root": {
        backgroundColor: "#EBF3F9",
        color: "#5899d1",
        fontWeight: "700",
        maxHeight: "50px !important",
        minHeight: "50px !important",
      },
      "& .MuiTypography-root": { fontWeight: 700 },
    },
    "& .MuiButton-root": { borderRadius: "0.125rem !important" },
  },
  noAllowance: {
    textAlign: "center",
    marginTop: theme.spacing(4),
    fontStyle: "italic",
    color: "#999",
  },
  buttonGroupSpacing: {
    marginBottom: "10px",
  },
}));

function StaffAgreementsV2() {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const { staffLabourAgreementStore } = useStore();
  const { id } = useParams();

  const {
    pagingStaffLabourAgreement,
    listStaffLabourAgreement,
    handleOpenCreateEdit,
    handleExportHDLD,
    searchObject,
    handleDelete,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    setOpenCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    setSelectedStaffLabourAgreement,
  } = staffLabourAgreementStore;

  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    staffLabourAgreementStore.openCreateEditPopup = false;
    if (id) {
      setIsLoading(true);
      searchObject.staffId = id;
      searchObject.staff = { id: id };
      pagingStaffLabourAgreement()
        .finally(() => {
          setIsLoading(false);
        });
    }
  }, [id]);

  const isAdmin = useMemo(() => {
    let roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some((role) => auth.indexOf(role) !== -1);
  }, []);

  async function handleOpenStaffLabourAgreementForm() {
    setOpenCreateEditPopup(true);
    setSelectedStaffLabourAgreement({ staff: { id: id } });
  }
  const columns = [
    {
      title: t("general.action"),
      minWidth: "48px",
      align: "center",
      render: (rowData) => {
        return (
          <div className="flex flex-middle justify-center">
            <Tooltip title="Cập nhật thông tin" placement="top">
              <IconButton size="small" onClick={function () {
                handleOpenCreateEdit(rowData?.id);
              }}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Xóa" placement="top">
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleDelete(rowData)
                }
              >
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Tải xuống hợp đồng lao động" arrow>
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleExportHDLD(rowData?.id)}
              >
                <Icon fontSize="small" color="blue">
                  description
                </Icon>
              </IconButton>
            </Tooltip>
          </div>
        );
      },
    },
    {
      title: t("agreements.labourAgreementNumber"),
      field: "labourAgreementNumber",
      align: "left",
    },
    {
      title: t("agreements.signedDate"),
      field: "signedDate",
      align: "left",
      render: data => data?.signedDate && (<span>{formatDate("DD/MM/YYYY", data?.signedDate)}</span>)
    },
    {
      title: t("agreements.startDate"),
      field: "startDate",
      align: "left",
      render: data => data?.startDate && (<span>{formatDate("DD/MM/YYYY", data?.startDate)}</span>)
    },
    {
      title: t("agreements.endDate"),
      field: "endDate",
      align: "left",
      render: data => data?.endDate && (<span>{formatDate("DD/MM/YYYY", data?.endDate)}</span>)
    },
    {
      title: t("agreements.contractType"),
      field: "contractType",
      align: "left",
      render: data => <span>{data?.contractType?.name}</span>
    },
    // {
    //   title: t("Mẫu bảng lương"),
    //   field: "salaryTemplateName",
    //   align: "left",
    //   render: data => <span>{data?.salaryTemplate?.name}</span>
    // },
  ];




  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray
          name="agreements"
          render={({ remove, replace, push }) => (
            <>
              {(isAdmin && values?.id) && (

                <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
                  className={classes.buttonGroupSpacing}
                >
                  <Button
                    startIcon={<AddIcon />}
                    type="button"
                    onClick={() => handleOpenStaffLabourAgreementForm()}
                  >
                    Thêm mới
                  </Button>
                </ButtonGroup>
              )}
              <Grid container spacing={2} className={classes.root}>
                <Grid item xs={12}>
                  {isLoading ? (
                    <p className="w-100 text-center">Đang tải dữ liệu...</p>
                  ) : listStaffLabourAgreement ? (
                    <GlobitsTable
                      data={listStaffLabourAgreement}
                      columns={columns}
                      maxWidth="100%"
                      nonePagination
                      selection={false}
                    />
                  ) : (
                    <p className="w-100 text-center">Chưa có phụ cấp</p>
                  )}
                </Grid>
              </Grid>

            </>
          )}
        />
        {openCreateEditPopup && (
          <StaffLabourAgreementCUForm hasStaff={true} />
        )}

        {openConfirmDeletePopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
          />
        )}

        {openConfirmDeleteListPopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeleteListPopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDeleteList}
            title={t("confirm_dialog.delete_list.title")}
            text={t("confirm_dialog.delete_list.text")}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
          />
        )}
      </Grid>
    </Grid>
  );
}

export default memo(observer(StaffAgreementsV2));
