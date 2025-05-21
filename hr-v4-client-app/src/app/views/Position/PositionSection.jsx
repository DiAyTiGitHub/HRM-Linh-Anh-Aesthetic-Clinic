import {
    Button, ButtonGroup, Grid, makeStyles
} from "@material-ui/core";
import {Add, Delete} from "@material-ui/icons";
import {FieldArray, useFormikContext} from "formik";
import {observer} from "mobx-react";
import React, {memo} from "react";
import {useTranslation} from 'react-i18next';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import {pagingPosition} from "./PositionService";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import LocalConstants from "../../LocalConstants";

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

function PositionSection() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values, setFieldValue} = useFormikContext();

    function handleAddNewRow(push) {
        const newItem = {
            position: null,
            relationshipType: null,
        };
        push(newItem);
    }

    return (<Grid container spacing={2}>
        <Grid item xs={12}>
            <FieldArray name="positionRelationShips">
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
                                        //  disabled={!hasEditPermission}
                                    >
                                        <Add color="white" className='mr-2 addIcon'/>
                                        Thêm vị trí phụ thuộc
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
                                    <th align="center" style={{width: "35%"}}>Vị trí</th>
                                    <th align="center" style={{width: "35%"}}>Loại quan hệ</th>
                                    <th align="center">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                {values?.positionRelationShips?.length > 0 ? (values?.positionRelationShips?.map((order, index) => (
                                    <PositionSectionRelationItem
                                        key={index}
                                        index={index}
                                        order={order}
                                        positionRelationShips={values?.positionRelationShips}
                                        nameSpace={`positionRelationShips[${index}]`}
                                        remove={() => remove(index)}
                                        push={() => push(index)}
                                        //  disabled={!hasEditPermission}
                                    />))) : (<tr className='row-table-body row-table-no_data'>
                                    <td colSpan={4} align='center' className="py-8">Chưa có phần tử nào
                                    </td>
                                </tr>)}
                                </tbody>
                            </table>
                        </section>
                    </Grid>

                </>)}
            </FieldArray>
            {/* </Form>
                    )}
                </Formik> */}
        </Grid>
    </Grid>)
}

const PositionSectionRelationItem = memo((props) => {
    const {
        index, positionRelationShips, order, remove, push, nameSpace, disabled
    } = props;

    const {
        setFieldValue, values

    } = useFormikContext();

    const {t} = useTranslation();

    const handleTabKeyPress = (event) => {
        if (event.key === 'Tab') {
            if ((Number(index) === Number(positionRelationShips?.length - 1))) {
                push();
            }
        }
    };

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    return (<tr className='row-table-body' key={index}>
        <td>
            <GlobitsPagingAutocompleteV2
                required
                name={withNameSpace("position")}
                api={pagingPosition}
                getOptionLabel={(option) => option?.name}
            />
        </td>
        <td>
            <GlobitsSelectInput
                required
                hideNullOption
                name={withNameSpace("relationshipType")}
                options={LocalConstants.RelationshipType.getListData()}
            />
        </td>

        {!disabled && <td align='center'>
                    <span
                        // tooltip={t("Xóa")}
                        className="pointer tooltip text-red"
                        style={{cursor: 'pointer'}}
                        onClick={remove}
                    >
                <Delete
                    className="text-red"
                />
            </span>
        </td>
        }

    </tr>)
})

export default memo(observer(PositionSection));
