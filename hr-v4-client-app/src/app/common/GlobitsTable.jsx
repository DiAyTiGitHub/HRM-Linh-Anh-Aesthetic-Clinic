import { makeStyles } from "@material-ui/core";
import MaterialTable from "material-table";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";
import GlobitsPagination from "./GlobitsPagination";
import { useMemo } from "react";

const useStyles = makeStyles ((theme) => ({
  globitsTableWraper:{
    width:"100%",
    "& td":{
      // borderBottom: 'unset !important',
      border:"1px solid #ccc",
      paddingLeft:"4px",
    },
    "& th":{
      // borderBottom: 'unset !important',
      border:"1px solid #ccc",
      fontWeight:"600",
      color:"#000000",
    },
    border:"0 !important",
    // borderRadius: "5px",
    overflow:"hidden",
    backgroundColor:"white",

    "& .MuiCheckbox-root":{
      display:"flex",
      justifyContent:"center",
      margin:0
    },

    "& .MuiPaper-elevation2":{
      boxShadow:"none",
    },

    "& .mat-mdc-row:hover":{
      backgroundColor:"red",
    },
  },
}));

function GlobitsTable (props) {
  const classes = useStyles ();

  const {t} = useTranslation ();

  const {
    data,
    columns,
    totalPages,
    handleChangePage,
    setRowsPerPage,
    pageSize,
    pageSizeOption,
    totalElements,
    page,
    selection,
    handleSelectList,
    maxWidth,
    nonePagination,
    maxHeight,
    colParent = false,
    specialStyleForLastRow,
    defaultExpanded = false,
    rowStyle:propRowStyle,
  } = props;

  // Memoize rowStyle để tránh tạo lại hàm không cần thiết
  const internalRowStyle = useMemo (() => {
    return (rowData, index) => {
      // Kiểm tra an toàn cho data
      if (!data || !Array.isArray (data)) {
        return {};
      }
      const isLastRow = index === data.length - 1;
      if (specialStyleForLastRow && isLastRow) {
        return {
          backgroundColor:"#fffacd", // Light yellow for "Tổng tiền"
          fontWeight:"bold",
          textAlign:"center",
          color:"#000",
          border:"1px solid #000",
        };
      }
      return {
        backgroundColor:!(index % 2 === 1)? "#fbfcfd" : "#ffffff",
        textAlign:"center",
        color:"red",
      };
    };
  }, [data, specialStyleForLastRow]);

  // Memoize filterCellStyle
  const filterCellStyle = useMemo (() => {
    return () => ({
      // Trả về object mặc định hoặc style cụ thể nếu cần
    });
  }, []);
  // Nếu propRowStyle là func, ưu tiên dùng prop
  const rowStyle = propRowStyle || internalRowStyle;
  return (
      <div className={classes.globitsTableWraper}>
        <MaterialTable
            data={data}
            columns={columns}
            style={{
              borderRadius:"10px",
            }}
            parentChildData={
              colParent?
                  (row, rows) => {
                    if (row?.parentId) {
                      var list = rows.find ((a) => a?.id === row?.parentId);
                      return list;
                    }
                    return null;
                  } : undefined
            }
            options={{
              selection:selection? true : false,
              sorting:false,
              actionsColumnIndex:-1,
              paging:false,
              search:false,
              toolbar:false,
              draggable:false,
              maxBodyHeight:maxHeight? maxHeight : "unset",
              headerStyle:{
                color:"#000",
                paddingLeft:"4px",
                paddingRight:!selection? "4px" : "unset",
                paddingTop:"8px",
                paddingBottom:"8px",
                fontSize:"14px",
                maxWidth:maxWidth? maxWidth : "auto",
                // "& nth-child(0)": {
                //   textAlign: "center",
                // },
                textAlign:"center",
              },
              rowStyle,
              filterCellStyle,
              defaultExpanded:defaultExpanded,
              // rowStyle: (rowData, index) => {
              //   const isLastRow = index === data.length - 1;
              //   // console.log("rowData", rowData);
              //   if (specialStyleForLastRow && isLastRow) {
              //     return {
              //       backgroundColor: "#fffacd", // Light yellow for "Tổng tiền"
              //       fontWeight: "bold",
              //       textAlign: "center",
              //       color: "#000",
              //       border: "1px solid #000", // Optional: Add a custom border for "Tổng tiền"
              //     };
              //   }

              //   return {
              //     backgroundColor: !(index % 2 === 1) ? "#fbfcfd" : "#ffffff",
              //     textAlign: "center",
              //     color: "red",
              //   };
              // },
              // filterCellStyle: (row, index) => {
              //   // console.log("filterCellStyle", row);
              //   // console.log("filterCellStyle", index);
              // },
            }}
            onSelectionChange={(rows) => {
              handleSelectList (rows);
            }}
            localization={{
              body:{
                emptyDataSourceMessage:`${t ("general.emptyDataMessageTable")}`,
              },
            }}
        />

        {!nonePagination && (
            <GlobitsPagination
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setRowsPerPage}
                pageSize={pageSize}
                pageSizeOption={pageSizeOption}
                totalElements={totalElements}
                page={page}
            />
        )}
      </div>
  );
}

GlobitsTable.propTypes = {
  ... GlobitsPagination.propTypes,
  data:PropTypes.array.isRequired,
  columns:PropTypes.arrayOf (
      PropTypes.shape ({
        field:PropTypes.string,
        title:PropTypes.string,
        minWidth:PropTypes.oneOfType ([PropTypes.string, PropTypes.number]),
        render:PropTypes.func,
        cellStyle:PropTypes.object,
        headerStyle:PropTypes.object,
        align:PropTypes.string,
      })
  ).isRequired,
  selection:PropTypes.bool,
  handleSelectList:PropTypes.func,
  maxWidth:PropTypes.oneOfType ([PropTypes.string, PropTypes.number]),
  maxHeight:PropTypes.oneOfType ([PropTypes.string, PropTypes.number]),
  nonePagination:PropTypes.bool,
  defaultExpanded:PropTypes.bool,
  rowStyle:PropTypes.func,
};

GlobitsTable.defaultProps = {
  data:[],
};

export default GlobitsTable;
