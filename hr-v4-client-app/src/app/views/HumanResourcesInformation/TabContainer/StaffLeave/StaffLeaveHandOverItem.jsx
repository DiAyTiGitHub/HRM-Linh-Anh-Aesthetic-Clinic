import { Button , ButtonGroup , Grid , makeStyles } from "@material-ui/core";
import { Add , Delete } from "@material-ui/icons";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { FieldArray , useFormikContext } from "formik";
import { observer } from "mobx-react";
import React , { memo } from "react";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaffLeave } from "./StaffLeaveService";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { pagingSalaryPeriod } from "../../../Salary/SalaryPeriod/SalaryPeriodService";

const useStyles = makeStyles(() => ({
  root:{
    background:"#E4f5fc" ,
    padding:"10px 15px" ,
    borderRadius:"5px" ,
  } ,
  groupContainer:{
    width:"100%" ,
    "& .MuiOutlinedInput-root":{
      borderRadius:"0!important" ,
    } ,
  } ,
  tableContainer:{
    marginTop:"16px" ,
    "& table":{
      border:"1px solid #ccc" ,
      borderCollapse:"collapse" ,
      "& td":{
        border:"1px solid #ccc" ,
      } ,
    } ,
  } ,
  tableHeader:{
    width:"100%" ,
    borderBottom:"1px solid #ccc" ,
    marginBottom:"8px" ,
    "& th":{
      border:"1px solid #ccc" ,
      padding:"8px 0 8px 4px" ,
    } ,
  } ,
  centerCheckBox:{
    display:"flex " ,
    justifyContent:"center " ,
  }
}));

const StaffLeaveHandOverItem = () => {
  const classes = useStyles();
  const {values} = useFormikContext();

  const handleAddNewRow = (push) => {
    const newItem = {
      displayOrder:null ,
      name:"" ,
      note:"" ,
      handoverDate:null ,
      isHandovered:false ,
    };
    push(newItem);
  };


  return (
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <FieldArray name="handleOverItems">
            {({push , remove}) => (
                <>
                  <Grid item xs={12}>
                    <Grid container spacing={1}>
                      <Grid item xs={12} sm={4} md={2} lg={1}>
                        <ButtonGroup color="container" aria-label="outlined primary button group">
                          <Button onClick={() => handleAddNewRow(push)} fullWidth>
                            <Add color="white" className="mr-2 addIcon"/>
                            Thêm
                          </Button>
                        </ButtonGroup>
                      </Grid>
                    </Grid>
                  </Grid>

                  <Grid item xs={12}>
                    <section className={classes.tableContainer}>
                      <table className={`w-100 ${classes.table}`}>
                        <thead>
                        <tr className={classes.tableHeader}>
                          <th className="text-center" style={{width:"10%"}}>Thao tác</th>
                          <th className="text-center" style={{width:"35%"}}>
                            Tên hạng mục<span style={{color:"red"}}> * </span>
                          </th>
                          {/* <h className="text-center" style={{width:"10%"}}>Thứ tự hiển thị<span
                              style={{color:"red"}}> * </span></h> */}
                          <th className="text-center" style={{width:"15%"}}>
                            Ngày bàn giao
                          </th>
                          <th className="text-center" style={{width:"15%"}}>Ghi chú</th>
                          <th className="text-center" style={{width:"5%"}}>Đã nộp</th>
                        </tr>
                        </thead>
                        <tbody>
                        {values?.handleOverItems?.length > 0 ? (
                            values.handleOverItems.map((_ , index) => (
                                <HandOverItemRow
                                    key={index}
                                    index={index}
                                    nameSpace={`handleOverItems[${index}]`}
                                    remove={() => remove(index)}
                                />
                            ))
                        ) : (
                            <tr className="row-table-body row-table-no_data">
                              <td colSpan={6} align="center" className="py-8">
                                Chưa có dữ liệu
                              </td>
                            </tr>
                        )}
                        </tbody>
                      </table>
                    </section>
                  </Grid>
                </>
            )}
          </FieldArray>
        </Grid>
      </Grid>
  );
};

const HandOverItemRow = memo(({index , nameSpace , remove}) => {
  const classes = useStyles();
  const withNameSpace = (field) => (field ? `${nameSpace}.${field}` : nameSpace);

  return (
      <tr className="row-table-body" key={index}>
        <td className="text-center">
          <Button onClick={remove} size="small" color="secondary">
            <Delete/>
          </Button>
        </td>
        <td>
          <GlobitsTextField name={withNameSpace("name")} required/>
        </td>
        {/* <td>
          <GlobitsNumberInput name={withNameSpace("displayOrder")} required/>
        </td> */}
        <td>
          <GlobitsDateTimePicker name={withNameSpace("handoverDate")}/>
        </td>
        <td>
          <GlobitsTextField name={withNameSpace("note")}/>
        </td>
        <td className={classes.centerCheckBox}>
          <GlobitsCheckBox
              name={withNameSpace("isHandovered")}
          />
        </td>
      </tr>
  );
});

export default memo(observer(StaffLeaveHandOverItem));
