import React, { memo } from "react";
import { observer } from "mobx-react";
import {
    makeStyles
} from "@material-ui/core";
import ChosenStaffDraggableTable from "./ChosenStaffDraggableTable";
import ChosenStaffRegularTable from "./ChosenStaffRegularTable";

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
        // overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        // width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));


function ChosenStaffList(props) {
    const {
        isDraggableTable = false,
        listSelectedStaffs,
        setListSelectedStaffs,
    } = props;

    return (
        <>
            {/* Bảng cho phép kéo thả các dòng dữ liệu đã chọn */}
            {isDraggableTable && (
                <ChosenStaffDraggableTable />
            )}

            {/* Bảng không cho phép kéo thả các dòng dữ liệu đã chọn */}
            {!isDraggableTable && (
                <ChosenStaffRegularTable listSelectedStaffs={listSelectedStaffs} setListSelectedStaffs={setListSelectedStaffs} />
            )}
        </>

    );
}

export default memo(observer(ChosenStaffList));