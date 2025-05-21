import {
    Button, ButtonGroup, Grid, makeStyles
} from "@material-ui/core";
import {Add, Delete} from "@material-ui/icons";
import {FieldArray, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo} from "react";
import {useTranslation} from 'react-i18next';
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import {pagingPositionTitle} from "../../../PositionTitle/PositionTitleService";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "16px", "& table": {
            border: "1px solid #ccc", borderCollapse: "collapse", "& td": {
                border: "1px solid #ccc",
            }
        }
    }, tableHeader: {
        width: "100%", borderBottom: "1px solid #ccc", marginBottom: "8px", "& th": {
            // width: "calc(100vw / 4)",
            border: "1px solid #ccc", padding: "8px 0 8px 4px"
        },
    },
}));

function TabRecruitmentInfoItemSection() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values, setFieldValue} = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            quantity: "",
            positionTitle: null,
        };
        push(newItem);
    }

    return (<Grid container spacing={2}>
        <Grid item xs={12}>
            <FieldArray name="recruitmentItems">
                {({insert, remove, push}) => (<>
                    <Grid item xs={12}>
                        <Grid container spacing={1} className=''>
                            <Grid item xs={12} sm={4}>
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        onClick={() => handleAddNewRow(push)}
                                        fullWidth
                                    >
                                        <Add color="white" className='mr-2 addIcon'/>
                                        Thêm mới vị trí tuyển dụng
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
                                    <th align="center" style={{width: "33%"}}>Vị trí cần tuyển</th>
                                    <th align="center" style={{width: "33%"}}>Số lượng</th>
                                    <th align="center" style={{width: "34%"}}>Hành động</th>

                                </tr>
                                </thead>
                                <tbody>
                                {values?.recruitmentItems?.length > 0 ? (values?.recruitmentItems?.map((order, index) => (
                                    <RecruitmentItem
                                        key={index}
                                        index={index}
                                        order={order}
                                        KPIItems={values?.recruitmentItems}
                                        nameSpace={`recruitmentItems[${index}]`}
                                        remove={() => remove(index)}
                                        push={() => push(index)}
                                    />))) : (<tr className='row-table-body row-table-no_data'>
                                    <td colSpan={3} align='center' className="py-8">Chưa có phần tử nào
                                    </td>
                                </tr>)}
                                </tbody>
                            </table>
                        </section>
                    </Grid>

                </>)}
            </FieldArray>
        </Grid>
    </Grid>)
}

const RecruitmentItem = memo((props) => {
    const {
        index, remove, nameSpace, disabled
    } = props;
    const {values, setFieldValue} = useFormikContext();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (<tr className='row-table-body' key={index}>

        <GlobitsPagingAutocompleteV2
            name={withNameSpace("positionTitle")}
            api={pagingPositionTitle}
        />
        <td>
            <GlobitsNumberInput
                name={withNameSpace("quantity")}
            />
        </td>
        {!disabled && <td align='center'>
                    <span
                        className="pointer tooltip text-red"
                        style={{cursor: 'pointer'}}
                        onClick={remove}
                    >
                        <Delete
                            className="text-red"
                        />
                    </span>
        </td>}

    </tr>)
})

export default memo(observer(TabRecruitmentInfoItemSection));
