import React, {memo, useEffect} from "react";
import {makeStyles,} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {formatDate} from "app/LocalFunction";
import GlobitsTable from "app/common/GlobitsTable";
import {observer} from "mobx-react";
import {useStore} from "app/stores";

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

function Assets() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {assetManagementStore} = useStore();
    const {getAssetByStaff, listAsset} = assetManagementStore.assetStore;

    const columns = [
        {
            title: "Công cụ/ dụng cụ",
            field: "product.name",
            minWidth: "150px",
        },
        {
            title: "Ngày bắt đầu dùng",
            field: "startDate",
            minWidth: "150px",
            render: (value) => formatDate("DD/MM/YYYY", value.startDate)
        },
        {
            title: "Ngày kết thúc",
            field: "endDate",
            minWidth: "150px",
            render: (value) => formatDate("DD/MM/YYYY", value.endDate)
        },
    ]
    useEffect(() => {
        if (values?.id) {
            getAssetByStaff(values?.id);
        }
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {listAsset?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={listAsset || []}/>
                ) : (
                    <h5 className="text-primary n-w">
                        Không có công cụ dụng cụ nào!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(Assets))