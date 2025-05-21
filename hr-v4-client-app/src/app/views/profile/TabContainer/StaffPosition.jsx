import React, { memo, useEffect } from "react";
import { makeStyles, } from "@material-ui/core";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import { observer } from "mobx-react";
import { useStore } from "app/stores";
import CheckIcon from "@material-ui/icons/Check";

const useStyles = makeStyles((theme) => ({
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
        marginTop: "8px",
    },
}));

function StaffPosition() {

    const { t } = useTranslation();
    const classes = useStyles();
    const { values } = useFormikContext();
    const { positionStore } = useStore();
    const {
        currentPositions,
        fetchPositions,
    } = positionStore;

    const columns = [
        {
            title: "Mã vị trí",
            field: "code",
            minWidth: "150px",
            render: (row) => <span>{row?.code}</span>,
        },
        {
            title: "Tên vị trí",
            field: "title",
            minWidth: "150px",
            render: (row) => <span>{row?.name}</span>,
        },
        {
            title: "Chức danh",
            field: "title",
            minWidth: "150px",
            render: (data) => <span className='px-4'>{`${data?.title?.name}`}</span>,
        },
        {
            title: "Đơn vị",
            field: "organization",
            minWidth: "150px",
            render: (row) => <span>{row?.department?.organization?.name}</span>,
        },
        {
            title: "Phòng ban",
            field: "department",
            minWidth: "150px",
            render: (row) => <span>{row?.department?.name}</span>,
        },

        {
            title: "Vị trí chính",
            field: "isMain",
            minWidth: "150px",
            align: "center",
            render: (data) => {
                if (data?.isMain) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                return "";
            },
        },
        {
            title: "Vị trí kiêm nhiệm",
            field: "isConcurrent",
            minWidth: "150px",
            align: "center",
            render: (data) => {
                if (data?.isConcurrent) return <CheckIcon fontSize='small' style={{ color: "green" }} />;
                return "";
            },
        },
    ];
    useEffect(() => {
        if (values?.id) {
            fetchPositions(values?.id);
        }
    }, [values?.id]);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {currentPositions?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={currentPositions || []} />
                ) : (
                    <h5 className="text-primary n-w">
                        Chưa có vị trí công tác nào!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffPosition))