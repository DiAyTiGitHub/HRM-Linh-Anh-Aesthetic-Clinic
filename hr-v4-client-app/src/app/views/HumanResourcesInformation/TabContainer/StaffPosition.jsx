import { Grid, makeStyles, IconButton, Tooltip, Icon } from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { FieldArray, useFormikContext } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import { useStore } from "app/stores";
import PositionCUForm from "app/views/Position/PositionCUForm";
import { pagingPosition } from "app/views/Position/PositionService";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

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

function StaffPosition() {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const { staffStore, positionStore } = useStore();
  const { id } = useParams();

  const {
    saveStaffAllowance,
    selectedPosition,
    listPosition,
    handleOpenCreateEdit,
    openCreateEditPopup,
    setListPosition,
    searchObject,
    handleSetSearchObject,
    handleDelete,
    openConfirmDeletePopup,
    handleConfirmDelete,
    handleClose,
    resetStore
  } = positionStore;

  const [data, setData] = useState(null);
  //const [hasPositions, setHasPositions] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function fetchPositions() {
      if (!id) return;
      setIsLoading(true);
      const newSearchObject = { ...searchObject, staffId: id, pageIndex: 1, pageSize: 9999 };
      try {
        handleSetSearchObject(newSearchObject);
        const { data } = await pagingPosition(newSearchObject);
        setData(data?.content);
      } catch (error) {
        console.error("Lỗi khi tải danh sách vị trí:", error);
      } finally {
        setIsLoading(false);
      }
    }
    fetchPositions();
  }, [id]);

  const isAdmin = useMemo(() => {
    let roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some((role) => auth.includes(role));
  }, []);
  
  const baseColumns = [
    {
      title: "Mã vị trí công tác",
      field: "code",
      width: "10%",
      render: (row) => <span>{row?.code}</span>,
      align: "center",
    },
    {
      title: "Tên vị trí công tác",
      field: "title",
      width: "20%",
      render: (row) => <span>{row?.title?.name}</span>,
      align: "center",
    },
    {
      title: "Chức danh",
      field: "title",
      width: "20%",
      render: (data) => data?.title?.name,
      align: "center",
    },
    {
      title: "Đơn vị",
      field: "organization",
      width: "15%",
      render: (row) => <span>{row?.department?.organization?.name}</span>,
      align: "center",
    },
    {
      title: "Phòng ban",
      field: "department",
      width: "15%",
      render: (row) => <span>{row?.department?.name}</span>,
      align: "center",
    },
  ];
  
  const actionColumn = {
    title: t("general.action"),
    width: "10%",
    align: "center",
    render: (rowData) => (
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
      
    ),
  };
  // Nếu là admin, thêm cột hành động vào đầu danh sách cột
  const columns = isAdmin ? [actionColumn, ...baseColumns] : baseColumns;
  
  return (
    <FieldArray
      name="postions"
      render={({ remove, replace, push }) => (
        <>
          {/* {(isAdmin && values?.id) && (
            <ButtonGroup
              color="container"
              aria-label="outlined primary button group"
              className={classes.buttonGroupSpacing}
            >
              <Button
                startIcon={<AddIcon />}
                type="button"
                onClick={() => setPosition(initialValues)}
              >
                Thêm mới
              </Button>
            </ButtonGroup>
          )} */}
          <Grid container spacing={2} className={classes.root}>
            <Grid item xs={12}>
              {isLoading ? (
                <p className="w-100 text-center">Đang tải dữ liệu...</p>
              ) : data && data.length > 0 ? (

                <GlobitsTable
                  data={data}
                  columns={columns}
                  maxWidth="100%"
                  nonePagination
                  selection={false}
                />
              ) : (
                <p className="w-100 text-center">Chưa có vị trí</p>
              )}
            </Grid>
          </Grid>


          {openCreateEditPopup === true && (
            <PositionCUForm />
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
      )}
    />
  );
}

export default memo(observer(StaffPosition));
