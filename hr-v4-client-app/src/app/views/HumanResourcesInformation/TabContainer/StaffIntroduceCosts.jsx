import { Grid, makeStyles, DialogContent, DialogActions, ButtonGroup, IconButton, Icon, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate, getDate } from "app/LocalFunction";
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
import { pagingHrIntroduceCost } from "app/views/HrIntroduceCost/HrIntroduceCostService";

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

function StaffIntroduceCosts() {
  const { t } = useTranslation();

  const classes = useStyles();

  const { values } = useFormikContext();

  const {
    staffStore,
    hrIntroduceCostStore

  } = useStore();

  const { id } = useParams();

  const {
    openCreateEditPopup,
    setOpenCreateEditPopup,
    setSelectedHrIntroduceCost,
    searchObject,
    handleSetSearchObject,
    openConfirmDeletePopup,
    handleConfirmDelete: handleConfirmDeleteIC,
    handleClose,
    handleOpenCreateEdit,
    handleDelete
  } = hrIntroduceCostStore;

  const [introduceCosts, setIntroduceCosts] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchHrIntroduceCosts();
  }, [id]);

  const fetchHrIntroduceCosts = async () => {
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

      const { data } = await pagingHrIntroduceCost(payload);

      setIntroduceCosts(data?.content);
    } catch (error) {
      console.error("Lỗi khi tải danh sách chi phí giới thiệu", error);
    } finally {
      setIsLoading(false);
    }
  };

  async function handleOpenHrIntroduceCostCUForm() {
    setSelectedHrIntroduceCost({ staff: { id: id } });
    setOpenCreateEditPopup(true);
  }

  const columns = [
    {
      title: t("general.action"),
      width: "5%",
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
      title: t("Thứ tự nhập xuất excel"),
      field: "periodOrder",
      width: "10%",
      render: (row) => <span>{row?.periodOrder}</span>,
      align: "center"
    },
    {
      title: t("Tháng tính giới thiệu"),
      field: "introducePeriod",
      //render: (row) => <span>{getDate(row?.introducePeriod)}</span>,
      render: row => <span>{formatDate("DD/MM/YYYY", row?.introducePeriod)}</span>,
      align: "center"
    },
    {
      title: t("Chi phí được hưởng"),
      field: "cost",
      render: (data) => (
        <span>
          {typeof data?.cost === "number"
            ? new Intl.NumberFormat("vi-VN", {
              style: "currency",
              currency: "VND",
            }).format(data.cost)
            : data?.cost}
        </span>
      ),
      align: "center"
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
      const response = await handleConfirmDeleteIC();

      if (!response) throw new Error();

      await fetchHrIntroduceCosts();
    }
    catch (error) {
      console.error(error);
    }


  }

  return (
    <>

      <Grid container spacing={2} className={classes.root}>
        {(isAdmin && values?.id) && (
          <Grid item xs={12} className="pb-0">
            <ButtonGroup
              color="container"
              aria-label="outlined primary button group"
              className={classes.buttonGroupSpacing}
            >
              <Tooltip title="Thêm mới chi phí giới thiệu cho nhân viên" placement="top" arrow>
                <Button
                  startIcon={<AddIcon />}
                  type="button"
                  onClick={handleOpenHrIntroduceCostCUForm}
                >
                  Thêm mới
                </Button>
              </Tooltip>

            </ButtonGroup>
          </Grid>
        )}

        <Grid item xs={12}>
          {isLoading ? (
            <p className="w-100 text-center">Đang tải dữ liệu...</p>
          ) : introduceCosts && introduceCosts.length > 0 ? (
            <GlobitsTable
              data={introduceCosts}
              columns={columns}
              maxWidth="100%"
              nonePagination
              selection={false}
            />
          ) : (
            <p className="w-100 text-center">Chưa có chi phí giới thiệu nào</p>
          )}
        </Grid>
      </Grid>


      {openCreateEditPopup && (
        <HrIntroduceCostCUForm
          staffId={values?.id}
          onSaved={fetchHrIntroduceCosts}
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

export default memo(observer(StaffIntroduceCosts));
