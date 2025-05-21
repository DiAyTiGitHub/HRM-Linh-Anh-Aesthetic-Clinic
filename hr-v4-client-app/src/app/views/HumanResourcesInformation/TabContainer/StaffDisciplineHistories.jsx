import { Grid, makeStyles, ButtonGroup, IconButton, Icon, Tooltip } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { useFormikContext } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import { Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import CheckIcon from "@material-ui/icons/Check";
import PersonBankAccountCUForm from "app/views/PersonBankAccount/PersonBankAccountCUForm";
import LoadingTabSkeleton from "app/common/Skeleton/SkeletonTab";
import { pagingStaffDisciplineHistory } from "app/views/StaffDisciplineHistory/StaffDisciplineHistoryService";

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

function StaffDisciplineHistories() {
  const { t } = useTranslation();

  const classes = useStyles();

  const { values } = useFormikContext();

  const {
    staffStore,
    staffDisciplineHistoryStore

  } = useStore();

  const { id } = useParams();

  const {
    openCreateEditPopup,
    setOpenCreateEditPopup,
    searchObject,
    handleSetSearchObject,
    openConfirmDeletePopup,
    handleConfirmDelete: handleConfirmDeleteSingle,
    handleClose,
    handleOpenCreateEdit,
    handleDelete,
    setSelectedPersonBankAccount
  } = staffDisciplineHistoryStore;

  const [disciplineHistories, setDisciplineHitories] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchStaffDispciplineHistories();
  }, [id]);

  const fetchStaffDispciplineHistories = async () => {
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

      const { data } = await pagingStaffDisciplineHistory(payload);

      setDisciplineHitories(data?.content);
    } catch (error) {
      console.error("Lỗi khi tải danh sách tài khoản ngân hàng", error);
    } finally {
      setIsLoading(false);
    }
  };

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
    // {
    //   title: "Ngân hàng",
    //   field: "bank",
    //   width: "20%",
    //   render: (row) => <span>{row?.bank?.name}</span>,
    //   align: "center"
    // },
    // {
    //   title: t("Tên tài khoản"),
    //   field: "bankAccountName",
    //   render: row => <span>{row?.bankAccountName}</span>,
    //   align: "center"
    // },
    // {
    //   title: t("Số tài khoản ngân hàng"),
    //   field: "bankAccountNumber",
    //   render: row => <span>{row?.bankAccountNumber}</span>,
    //   align: "center"
    // },
    // {
    //   title: t("Chi nhánh"),
    //   field: "bankBranch",
    //   render: row => <span>{row?.bankBranch}</span>,
    //   align: "center"
    // },

    // {
    //   title: "TK chính",
    //   field: "isMain",
    //   width: "10%",
    //   align: "center",
    //   render: (data) => {
    //     if (data?.isMain) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
    //     return "";
    //   },
    // },

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

      await fetchStaffDispciplineHistories();
    }
    catch (error) {
      console.error(error);
    }
  }

  async function handleOpenCUPersonBankAccount() {
    setSelectedPersonBankAccount({ person: values });
    setOpenCreateEditPopup(true);
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
              <Tooltip title="Thêm mới tài khoản ngân hàng cho nhân viên" placement="top" arrow>
                <Button
                  startIcon={<AddIcon />}
                  type="button"
                  onClick={handleOpenCUPersonBankAccount}
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
          ) : disciplineHistories && disciplineHistories.length > 0 ? (
            <GlobitsTable
              data={disciplineHistories}
              columns={columns}
              maxWidth="100%"
              nonePagination
              selection={false}
            />
          ) : (
            <p className="w-100 text-center">Chưa có tài khoản ngân hàng</p>
          )}
        </Grid>
      </Grid>


      {openCreateEditPopup && (
        <PersonBankAccountCUForm
          person={values}
          handleAfterSubmit={fetchStaffDispciplineHistories}
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

export default memo(observer(StaffDisciplineHistories));
