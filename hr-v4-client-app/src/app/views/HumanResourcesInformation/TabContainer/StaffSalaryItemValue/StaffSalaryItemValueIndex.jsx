import { Button, ButtonGroup, Grid, Icon, IconButton, makeStyles, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import CheckIcon from "@material-ui/icons/Check";
import { formatDate, formatVNDMoney } from "app/LocalFunction";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import ListSalaryItemByStaff from "./ListSalaryItemByStaff";
import StaffSalaryItemValueTabForm from "./StaffSalaryItemValueTabForm";
// import StaffSalaryItemValueListTab from "./StaffSalaryItemValueListTab";

const useStyles = makeStyles ((theme) => ({
  root:{
    "& .MuiAccordion-rounded":{
      borderRadius:"5px",
    },

    "& .MuiPaper-root":{
      borderRadius:"5px",
    },

    "& .MuiAccordionSummary-root":{
      borderRadius:"5px", // backgroundColor: "#EBF3F9",
      color:"#5899d1 ",
      fontWeight:"400",

      "& .MuiTypography-root":{
        fontSize:"1rem",
      },
    },

    "& .Mui-expanded":{
      "& .MuiAccordionSummary-root":{
        backgroundColor:"#EBF3F9",
        color:"#5899d1 ", // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
        fontWeight:"700",
        maxHeight:"50px !important",
        minHeight:"50px !important",
      },
      "& .MuiTypography-root":{
        fontWeight:700,
      },
    },

    "& .MuiButton-root":{
      borderRadius:"0.125rem !important",
    },
  },
  listItem:{
    borderRadius:4,
    paddingLeft:theme.spacing (2),
    paddingRight:theme.spacing (2),
    "&:hover":{
      backgroundColor:theme.palette.action.hover,
    },
  },
  selected:{
    backgroundColor:theme.palette.action.selected,
    "&:hover":{
      backgroundColor:theme.palette.action.selected,
    },
  },
  noScrollbar:{
    "&::-webkit-scrollbar":{
      display:"none",
    },
    "-ms-overflow-style":"none", // IE và Edge
    "scrollbar-width":"none", // Firefox
  },
}));

export default observer (function StaffSalaryItemValueIndex () {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {id} = useParams ();
  const {staffSalaryItemValueStore, staffStore, salaryItemStore} = useStore ();

  const {getStaff, selectedStaff, resetStore:resetStaffStore} = staffStore;
  const {
    handleSelectedSalaryItem,
    selectedSalaryItem,
    resetStore:resetSalaryItemStore,
    getListByStaffId,
  } = salaryItemStore;
  const {
    listStaffSalaryItemValue,
    handleOpenCreate,
    openConfirmDeletePopup,
    handleConfirmDeleteNonPaging,
    handleClose,
    pagingStaffSalaryItemValue,
    resetStore:resetStaffSalaryItemValueStore,
  } = staffSalaryItemValueStore;

  useEffect (() => {
    if (id) {
      getStaff (id);
    }
  }, [id]);

  const fetchStaffSalaryItemValue = async (values) => {
    if (selectedStaff?.id && selectedSalaryItem?.id) {
      const dto = {
        staffId:selectedStaff?.id,
        salaryItemId:selectedSalaryItem?.id,
      };
      pagingStaffSalaryItemValue (dto);
    } else if (values?.salaryItem?.id && values?.staff?.id) {
      const dto = {
        staffId:values?.staff?.id,
        salaryItemId:values?.salaryItem?.id,
      };
      await getListByStaffId (values?.staff?.id);
    }
  };

  const resetStore = () => {
    resetStaffStore ();
    resetSalaryItemStore ();
    resetStaffSalaryItemValueStore ();
  };

  useEffect (() => {
    fetchStaffSalaryItemValue ();
  }, [selectedStaff?.id, selectedSalaryItem?.id]);

  useEffect (() => {
    return resetStore;
  }, []);

  return (
      <Grid container spacing={2}>
        <Grid
            item
            xs={2}
            style={{height:"calc(100vh - 300px)", overflow:"hidden"}}
            className={classes.noScrollbar}>
          <ListSalaryItemByStaff/>
        </Grid>

        <Grid item xs={10}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <ButtonGroup color='container' aria-label='outlined primary button group'>
                <Tooltip arrow placement='top' title={"Thêm giá trị phần tử lương"}>
                  <Button
                      startIcon={<AddIcon/>}
                      type='button'
                      onClick={() =>
                          handleOpenCreate ({
                            fromDate:new Date (),
                            staff:selectedStaff,
                            salaryItem:selectedSalaryItem,
                            value:selectedSalaryItem?.defaultValue || 0,
                            // calculationType: selectedSalaryItem?.calculationType,
                          })
                      }>
                    Thêm giá trị phần tử lương
                  </Button>
                </Tooltip>
              </ButtonGroup>
            </Grid>

            <Grid item xs={12}>
              <StaffSalaryItemValueListTab data={listStaffSalaryItemValue || []}/>
            </Grid>
          </Grid>
        </Grid>
        <StaffSalaryItemValueTabForm handleAfterSave={fetchStaffSalaryItemValue}/>

        {openConfirmDeletePopup && (
            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDeleteNonPaging}
                handleAfterConfirm={fetchStaffSalaryItemValue}
                title={t ("confirm_dialog.delete.title")}
                text={t ("confirm_dialog.delete.text")}
                agree={t ("confirm_dialog.delete.agree")}
                cancel={t ("confirm_dialog.delete.cancel")}
            />
        )}
      </Grid>
  );
});

function MaterialButton (props) {
  const {item} = props;
  return (
      <div>
        <IconButton size='small' onClick={() => props.onSelect (item, 0)}>
          <Icon fontSize='small' color='primary'>
            edit
          </Icon>
        </IconButton>
        <IconButton size='small' onClick={() => props.onSelect (item, 1)}>
          <Icon fontSize='small' color='secondary'>
            delete
          </Icon>
        </IconButton>
      </div>
  );
}

function StaffSalaryItemValueListTab (props) {
  const {data, onSelect} = props;
  const {t} = useTranslation ();

  const {
    staffSalaryItemValueStore,
    pageSize,
    totalStaffPages,
    handleChangePageSelectMultipleStaffs,
    setRowsPerPageSelectMultipleStaffs,
    totalStaffElements,
    pageIndex,
  } = useStore ();
  const {handleOpenCreateEdit, handleDelete} = staffSalaryItemValueStore;
  const columns = [
    {
      title:"STT",
      field:"index",
      align:"center",
      width:"5%",
      render:(rowData) => rowData.tableData.id + 1,
    },
    {
      title:"Ngày bắt đầu",
      field:"fromDate",
      align:"center",
      render:(row) => <span className='px-2'>{formatDate ("DD/MM/YYYY", row?.fromDate)}</span>,
    },
    {
      title:"Ngày kết thúc",
      field:"toDate",
      align:"center",
      render:(row) => (
          <span className='px-2'>
                    {row?.toDate? formatDate ("DD/MM/YYYY", row?.toDate) : row?.isCurrent? "Nay" : ""}
                </span>
      ),
    },

    {
      title:"Giá trị",
      field:"value",
      minWidth:"120px",
      align:"center",
      render:(rowData) => (
          <span className='px-6'>
                    <strong>{formatVNDMoney (rowData?.value)}</strong>
                </span>
      ),
    },

    {
      title:"Đang có hiệu lực",
      field:"isCurrent",
      align:"center",
      render:(rowData) => (rowData?.isCurrent? <CheckIcon fontSize='small' style={{color:"green"}}/> : ""),
    },
    {
      title:"Thao tác",
      field:"action",
      align:"left",
      render:(rowData) => (
          <MaterialButton
              item={rowData}
              onSelect={(item, type) => {
                if (type === 0) {
                  handleOpenCreateEdit (item?.id);
                }
                if (type === 1) {
                  handleDelete (item);
                }
              }}
          />
      ),
    },
  ];

  return (
      <GlobitsTable
          data={data || []}
          columns={columns}
          totalPages={totalStaffPages}
          handleChangePage={handleChangePageSelectMultipleStaffs}
          setRowsPerPage={setRowsPerPageSelectMultipleStaffs}
          pageSize={pageSize}
          pageSizeOption={[5, 10, 20, 25, 50]}
          totalElements={totalStaffElements}
          page={pageIndex}
      />
  );
}
