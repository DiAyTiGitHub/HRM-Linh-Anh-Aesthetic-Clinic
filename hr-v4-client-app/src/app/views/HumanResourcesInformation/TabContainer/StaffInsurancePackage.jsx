import { Grid, makeStyles, DialogContent, DialogActions, ButtonGroup, IconButton, Icon, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate, formatVNDMoney, getDate } from "app/LocalFunction";
import { FieldArray, useFormikContext, Formik, Form } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import { Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import { useStore } from "app/stores";
import HrIntroduceCostCUForm from "app/views/HrIntroduceCost/HrIntroduceCostCUForm";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import CheckIcon from "@material-ui/icons/Check";
import LoadingTabSkeleton from "app/common/Skeleton/SkeletonTab";
import SkeletonTable from "app/common/Skeleton/SkeletonTable";
import { pagingInsurancePackage } from "app/views/InsurancePackage/InsurancePackageService";
import { pagingStaffInsurancePackage } from "app/views/StaffInsurancePackage/StaffInsurancePackageService";
import StaffInsurancePackageCUForm from "app/views/StaffInsurancePackage/StaffInsurancePackageCUForm";

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

function StaffInsurancePackage() {
  const { t } = useTranslation();

  const classes = useStyles();

  // const { values } = useFormikContext();

  const {
    staffInsurancePackageStore
  } = useStore();

  const { id } = useParams();

  const {
    openCreateEditPopup,
    setOpenCreateEditPopup,
    searchObject,
    openConfirmDeletePopup,
    handleConfirmDelete: handleConfirmDeleteSingle,
    handleClose,
    handleOpenCreateEdit,
    handleDelete,
    setSelectedStaffInsurancePackage
  } = staffInsurancePackageStore;

  const [staffInsurancePackages, setStaffInsurancePackages] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchStaffInsurancePackages();
  }, [id]);

  const fetchStaffInsurancePackages = async () => {
    if (!id) return;
    setIsLoading(true);
    try {

      const payload = {
        ...searchObject,
        staffId: id,
        staff: {
          id: id,
        },
        pageIndex: 1,
        pageSize: 9999
      };
      const { data } = await pagingStaffInsurancePackage(payload);
      setStaffInsurancePackages(data?.content);
    } catch (error) {
      console.error("Lỗi khi tải danh sách gói bảo hiểm", error);
    } finally {
      setIsLoading(false);
    }
  };

  function renderInsuranceAmount({ value, percentageKey }) {
    const insuranceAmount = value?.insuranceAmount || 0;
    const percentage = value?.[percentageKey] || 0;
    const currency = value?.id ? '' : ' VNĐ';
    const calculatedAmount = (insuranceAmount * percentage) / 100;

    // Nếu là dòng tổng (không có id) và không có phần trăm thì không hiển thị gì
    if (!value?.id && percentage === 0) return null;

    return (
      <span style={{ whiteSpace: 'pre-line' }}>
        {calculatedAmount.toLocaleString()} {currency}
        {percentage ? `\n(${percentage}%)` : ''}
      </span>
    );
  }

  const columns = [
    {
      title: t("general.action"),
      width: "15%",
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
          </div>
        );
      },
    },
    {
      title: "Tên gói bảo hiểm",
      field: "insurancePackage.name",
      width: "20%",
      render: (row) => <span>{row?.insurancePackage?.name}</span>,
      align: "center"
    },
    {
      title: t("Ngày bắt đầu đóng bảo hiểm"),
      field: "startDate",
      render: row => <span>{getDate(row?.startDate)}</span>,
      align: "center"
    },
    {
      title: t("Ngày kết thúc đóng bảo hiểm"),
      field: "endDate",
      render: row => <span>{getDate(row?.endDate)}</span>,
      align: "center"
    },
    {
      title: t("Mức tham gia bảo hiểm"),
      field: "insuranceAmount",
      minWidth: "150px",
      render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.insuranceAmount)}`}</span>,
    },
    {
      title: t("Mức đền bù bảo hiểm"),
      field: "compensationAmount",
      minWidth: "150px",
      render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.compensationAmount)}`}</span>,
    },
    // {
    //   title: t("Tỷ lệ nhân viên đóng bảo hiểm"),
    //   field: "staffPercentage",
    //   minWidth: "150px",
    //   render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.staffPercentage)}%`}</span>,
    // },
    {
      align: "center",
      title: "Số tiền nhân viên đóng bảo hiểm",
      field: "insuranceAmountOfStaffPercentage",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'staffPercentage',
        });
      }
    },
    // {
    //   title: t("Tỷ lệ công ty đóng bảo hiểm"),
    //   field: "orgPercentage",
    //   minWidth: "150px",
    //   render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.orgPercentage)}%`}</span>,
    // },
    {
      align: "center",
      title: "Số tiền công ty đóng bảo hiểm",
      field: "insuranceAmountOfOrgPercentage",
      minWidth: "120px",
      render: function (value) {
        return renderInsuranceAmount({
          value,
          percentageKey: 'orgPercentage',
        });
      }
    },
    {
      title: "Có đóng cho thân nhân người lao động",
      field: "hasFamilyParticipation",
      width: "10%",
      align: "center",
      render: (data) => {
        if (data?.hasFamilyParticipation) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
        return "";
      },
    },

  ];

  const isAdmin = useMemo(() => {
    let roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some((role) => auth.indexOf(role) !== -1);
  }, []);


  async function handleConfirmDelete() {
    try {
      const response = await handleConfirmDeleteSingle();

      if (!response) throw new Error();

      await fetchStaffInsurancePackages();
    }
    catch (error) {
      console.error(error);
    }
  }

  async function handleOpenCUStaffInsurancePackage() {
    setSelectedStaffInsurancePackage({ staffId: id, staff: { staffId: id } });
    setOpenCreateEditPopup(true);
  }

  return (
    <>

      <Grid container spacing={2} className={classes.root}>
        {(isAdmin && id) && (
          <Grid item xs={12} className="pb-0">
            <ButtonGroup
              color="container"
              aria-label="outlined primary button group"
              className={classes.buttonGroupSpacing}
            >
              <Tooltip title="Thêm mới tài khoản ngân hàng cho nhân viên" placement="top" arrow>
                <Button
                  startIcon={<AddIcon />}
                  type="button"
                  onClick={handleOpenCUStaffInsurancePackage}
                >
                  Thêm mới
                </Button>
              </Tooltip>

            </ButtonGroup>
          </Grid>
        )}

        <Grid item xs={12}>
          {isLoading ? (
            <LoadingTabSkeleton />
          ) : staffInsurancePackages && staffInsurancePackages.length > 0 ? (
            <GlobitsTable
              data={staffInsurancePackages}
              columns={columns}
              maxWidth="100%"
              nonePagination
              selection={false}
            />
          ) : (
            <p className="w-100 text-center">Chưa có gói bảo hiểm</p>
          )}
        </Grid>
      </Grid>


      {openCreateEditPopup && (
        <StaffInsurancePackageCUForm
          staffId={id}
          handleAfterSubmit={fetchStaffInsurancePackages}
        />
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
    </>
  );
}

export default memo(observer(StaffInsurancePackage));
