import React, {memo, useEffect} from "react";
import {makeStyles,} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {formatDate} from "app/LocalFunction";
import GlobitsTable from "app/common/GlobitsTable";
import {observer} from "mobx-react";
import {useStore} from "app/stores";
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

function StaffPersonBankAccount() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {personBankAccountStore} = useStore();
    const {
        handleSetSearchObject,
        listPersonBankAccounts,
        pagingPersonBankAccount,
        searchObject
    } = personBankAccountStore;
    const columns = [
        {
            title: "Ngân hàng",
            field: "bank",
            minWidth: "150px",
            render: (row) => <span>{row?.bank?.name}</span>,
        },
        {
            title: t("Tên tài khoản"),
            field: "bankAccountName",
            minWidth: "150px",
            render: row => <span>{row?.bankAccountName}</span>,
        },
        {
            title: t("Số tài khoản ngân hàng"),
            field: "bankAccountNumber",
            minWidth: "150px",
            render: row => <span>{row?.bankAccountNumber}</span>,
        },
        {
            title: t("Chi nhánh"),
            field: "bankBranch",
            minWidth: "150px",
            render: row => <span>{row?.bankBranch}</span>,
        },

        {
            title: "TK chính",
            field: "isMain",
            width: "10%",
            minWidth: "150px",
            render: (data) => {
                if (data?.isMain) return <CheckIcon fontSize='small' style={{color: "green"}}/>;
                return "";
            },
        },
    ];

    useEffect(() => {
        if (values?.id) {
            const payload = {
                ...searchObject,
                staffId: values?.id,
                staff: {
                    id: values?.id,
                },
                pageIndex: 1,
                pageSize: 9999
            };
            handleSetSearchObject(payload);
            pagingPersonBankAccount()
        }
    }, [values?.id]);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {listPersonBankAccounts?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={listPersonBankAccounts || []}/>
                ) : (
                    <h5 className="text-primary n-w">
                        Chưa có tài khoản ngân hàng!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffPersonBankAccount))