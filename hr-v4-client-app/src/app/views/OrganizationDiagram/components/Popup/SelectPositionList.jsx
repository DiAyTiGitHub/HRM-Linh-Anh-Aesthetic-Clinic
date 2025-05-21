import { makeStyles, Radio } from "@material-ui/core";
import GlobitsPagination from "app/common/GlobitsPagination";
import { useStore } from "app/stores";
import MaterialTable from "material-table";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles((theme) => ({
    globitsTableWraper: {
        width: "100%",
        "& td": {
            // borderBottom: 'unset !important',
            border: "1px solid #ccc",
            paddingLeft: "4px",
        },
        "& th": {
            // borderBottom: 'unset !important',
            border: "1px solid #ccc",
            fontWeight: "600",
            color: "#000000",
        },
        border: "0 !important",
        // borderRadius: "5px",
        overflow: "hidden",
        backgroundColor: "white",

        "& .MuiCheckbox-root": {
            display: "flex",
            justifyContent: "center",
            margin: 0,
        },

        "& .MuiPaper-elevation2": {
            boxShadow: "none",
        },

        "& .mat-mdc-row:hover": {
            backgroundColor: "red",
        },
    },
}));
function SelectPositionList({ handleSelect }) {
    const { positionStore } = useStore();
    const { t } = useTranslation();

    const {
        listPosition,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        updatePageData,
    } = positionStore;

    function handleSelectItem(_, department) {
        handleSelect(department);
    }

    const columns = [
        {
            title: t("general.popup.select"),
            render: (rowData) => (
                <Radio
                    id={`radio${rowData?.id}`}
                    name='radSelected'
                    value={rowData?.id}
                    onClick={(event) => handleSelectItem(event, rowData)}
                />
            ),
        },
        {
            title: "Mã vị trí",
            field: "code",
            align: "left",
        },
        {
            title: "Tên vị trí",
            field: "name",
            align: "left",
        },
        {
            title: "Chức danh",
            field: "title",
            render: (data) => data?.title?.name,
            align: "left",
        },
        {
            title: "Đơn vị",
            field: "organization",
            render: (data) => data?.department?.organization?.name,
            align: "left",
        },
        {
            title: "Phòng ban",
            field: "department",
            render: (data) => data?.department?.name,
            align: "left",
        },
        {
            title: "Nhân viên",
            field: "staff.displayName",
            align: "left",
            render: (data) => {
                const displayName = data?.staff?.displayName ?? "";
                const staffCode = data?.staff?.staffCode ?? "";
                return displayName && staffCode
                    ? `${displayName} - ${staffCode}`
                    : displayName || staffCode || "Vacant";
            },
        },
    ];

    useEffect(function () {
        const initializeSearchObject = {
            pageSize: 10,
            pageIndex: 1,
        };

        updatePageData(initializeSearchObject);
    }, []);
    const classes = useStyles();
    return (
        <div className={classes.globitsTableWraper}>
            <MaterialTable
                data={listPosition}
                columns={columns}
                parentChildData={(row, rows) => {
                    var list = rows.find((a) => a?.id === row?.parentId);
                    return list;
                }}
                options={{
                    selection: false,
                    actionsColumnIndex: -1,
                    paging: false,
                    search: false,
                    toolbar: false,
                    maxBodyHeight: "300px",
                    headerStyle: {
                        color: "#000",
                        paddingLeft: "4px",
                        paddingTop: "8px",
                        paddingBottom: "8px",
                        fontSize: "14px",
                        // "& nth-child(0)": {
                        //   textAlign: "center",
                        // },
                        textAlign: "center",
                    },
                    rowStyle: (rowData, index) => ({
                        backgroundColor: index % 2 === 1 ? "rgb(237, 245, 251)" : "#FFF",
                    }),
                }}
                onSelectionChange={(rows) => {
                    this.data = rows;
                }}
            />
            <GlobitsPagination
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setRowsPerPage}
                pageSize={rowsPerPage}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={page}
            />
        </div>
    );
}

export default memo(observer(SelectPositionList));
