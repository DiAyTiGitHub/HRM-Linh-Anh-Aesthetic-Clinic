import React, {memo, useEffect} from "react";
import {makeStyles,} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import {observer} from "mobx-react";
import {useStore} from "app/stores";
import PreviewFile from "../../StaffDocumentItem/PreviewFile";

const useStyles = makeStyles((theme) => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "8px",
    },
}));

function StaffSignature() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffSignatureStore} = useStore();
    const {
        pagingStaffSignature, staffSignatureList, setCurrentStaffId, resetStore,
    } = staffSignatureStore;

    const columns = [
        {
            title: "Tệp đính kèm",
            field: "file",
            minWidth: "150px",
            render: (rowData) => {
                if (rowData?.file) {
                    return (<div className='flex flex-middle justify-center'>
                        <PreviewFile
                            fileProp={rowData?.file}
                            showPreview={true}
                            showDowload={true}
                            showDelete={false}
                            showName={false}
                        />
                    </div>);
                } else {
                    return null;
                }
            },
        },
        {
            title: t("Tên"),
            field: "name",
            minWidth: "150px",
        }, {
            title: t("Mã"),
            field: "code",
            minWidth: "150px",
        },

        {
            title: t("Mô tả"),
            field: "description",
            minWidth: "150px",
        },
    ]
    useEffect(() => {
        staffSignatureStore.openCreateEditPopup = false;

        if (values?.id) {
            pagingStaffSignature();
            setCurrentStaffId(values?.id);
        }
        setCurrentStaffId(values?.id);

        return resetStore;
    }, [values?.id]);
    return (<div className={classes.groupContainer}>
        <div className={classes.tableContainer}>
            {staffSignatureList?.length > 0 ? (<GlobitsTable
                nonePagination
                columns={columns}
                data={staffSignatureList || []}/>) : (<h5 className="text-primary n-w">
                Chưa có mẫu chữ ký!
            </h5>)}
        </div>
    </div>);
};

export default memo(observer(StaffSignature))