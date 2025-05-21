import React, {useEffect, useState} from "react";
import {FieldArray, Form, Formik} from "formik";
import {
    Box,
    Button, Collapse,
    DialogActions,
    DialogContent,
    Grid,
    IconButton, makeStyles,
    Table, TableBody,
    TableCell,
    TableHead,
    TableRow, Typography
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../../../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import GlobitsTextField from "../../../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../../../../common/GlobitsPopupV2";
import {EvaluationTemplateContentType} from "../../../../../LocalConstants";
import GlobitsSelectInput from "../../../../../common/form/GlobitsSelectInput";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import Paper from "@material-ui/core/Paper";
import ExpandLessIcon from "@material-ui/icons/ExpandLess";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import GlobitsPagingAutocomplete from "../../../../../common/form/GlobitsPagingAutocomplete";
import {pagingEvaluationItems} from "../EvaluationItem/EvaluationItemService";
const useStyles = makeStyles({
    table: {
        minWidth: 650,
        "& .MuiTableCell-root": {
            border: "2px solid rgba(224, 224, 224, 1)"
        }
    }
});
export default observer(function EvaluationTemplateForm(props) {
    const classes = useStyles();

    const {evaluationTemplateStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createEvaluationTemplate,
        selectedEvaluationTemplate,
        intactEvaluationTemplate
    } = evaluationTemplateStore;

    const [evaluationTemplate, setEvaluationTemplate] = useState(intactEvaluationTemplate);

    const validationSchema = Yup.object({
        code: Yup.string().nullable().required(t("validation.required")),
        name: Yup.string().nullable().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedEvaluationTemplate) setEvaluationTemplate(selectedEvaluationTemplate);
        else setEvaluationTemplate(intactEvaluationTemplate);
    }, [selectedEvaluationTemplate]);

    return (
        <GlobitsPopupV2
            size={"md"}
            open={props.open}
            noDialogContent
            title={
                (selectedEvaluationTemplate?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("evaluationTemplate.title")
            }
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    ...evaluationTemplate,
                    items: (evaluationTemplate.items || []).map(item => ({
                        ...item,
                        items: item.items || [], // đảm bảo items luôn là mảng
                        child: item.child || false // thêm trạng thái mở rộng
                    })),
                }}
                onSubmit={(values) => createEvaluationTemplate(values)
                    // values.id?.length === 0 || !values.id
                    //     ? createEvaluationItem(values)
                    //     : editEvaluationItem(values)
                }
            >
                {({isSubmitting, values, setFieldValue}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>{t("evaluationTemplate.name")}<span
                                                style={{color: "red"}}> * </span></span>}
                                            name="name"
                                        />
                                    </Grid>

                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>{t("evaluationTemplate.code")}<span
                                                style={{color: "red"}}> * </span></span>}
                                            name="code"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <FieldArray name="items">
                                            {({push, remove, form}) => (
                                                <Paper elevation={3} style={{padding: "24px", borderRadius: "12px"}}>
                                                    <Typography variant="h6" gutterBottom>Danh sách đánh
                                                        giá</Typography>
                                                    <Table className={classes.table} size="small">
                                                        <TableHead>
                                                            <TableRow sx={{backgroundColor: "#f5f5f5"}}>
                                                                <TableCell align="center" width="10%">STT</TableCell>
                                                                <TableCell>Loại đánh giá</TableCell>
                                                                <TableCell>Loại nội dung</TableCell>
                                                                <TableCell align="center" width="30%">Hành
                                                                    động</TableCell>
                                                            </TableRow>
                                                        </TableHead>
                                                        <TableBody>
                                                            {form.values.items && form.values.items.map((item, index) => (
                                                                <RecursiveItem
                                                                    key={index}
                                                                    item={item}
                                                                    index={index}
                                                                    parentPath="items"
                                                                    setFieldValue={setFieldValue}
                                                                    remove={remove}
                                                                />
                                                            ))}
                                                        </TableBody>
                                                    </Table>

                                                    <Grid container justifyContent="flex-end" className="mt-12">
                                                        <Button
                                                            variant="outlined"
                                                            color="primary"
                                                            startIcon={<AddIcon/>}
                                                            onClick={() =>
                                                                push({
                                                                    numberOrder: form.values.items.length + 1,
                                                                    type: "",
                                                                    items: [],
                                                                    child: false
                                                                })
                                                            }
                                                        >
                                                            Thêm dòng
                                                        </Button>
                                                    </Grid>
                                                </Paper>
                                            )}
                                        </FieldArray> </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className="dialog-footer">
                            <DialogActions className="p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={handleClose}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon/>}
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        type="submit"
                                    >
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});
const RecursiveItem = ({item, index, parentPath, setFieldValue, remove}) => {
    const path = `${parentPath}.${index}`;

    // Tự động cập nhật numberOrder
    useEffect(() => {
        setFieldValue(`${path}.numberOrder`, index + 1);
    }, [index, path, setFieldValue]);

    return (
        <React.Fragment>
            <TableRow>
                <TableCell align="center">
                    {index + 1} {/* Hiển thị số thứ tự tự động */}
                </TableCell>
                <TableCell>
                    <GlobitsPagingAutocomplete
                        name={`${path}.item`}
                        customData={'data'}
                        displayData='name'
                        api={pagingEvaluationItems}
                        keyValue="value"
                    />
                </TableCell>
                <TableCell>
                    <GlobitsSelectInput
                        name={`${path}.contentType`}
                        label=""
                        options={EvaluationTemplateContentType.map((item) => ({
                            value: item.name,
                            name: item.value,
                        }))}
                        keyValue="value"
                    />
                </TableCell>
                <TableCell align="center">
                    <Grid container spacing={1} justifyContent="center">
                        <Grid item>
                            <IconButton
                                onClick={() => remove(index)}
                                color="error"
                                size="small"
                            >
                                <DeleteIcon/>
                            </IconButton>
                        </Grid>
                        <Grid item>
                            <IconButton
                                onClick={() => {
                                    setFieldValue(`${path}.child`, !item.child);
                                }}
                                color="primary"
                                size="small"
                            >
                                {item.child ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
                            </IconButton>
                        </Grid>
                    </Grid>
                </TableCell>
            </TableRow>

            {item.child && (
                <TableRow>
                    <TableCell colSpan={4} style={{padding: 0}}>
                        <Collapse in={item.child} timeout="auto" unmountOnExit>
                            <Box sx={{margin: 5}}>
                                <Typography variant="subtitle1">
                                    Danh sách mục con
                                </Typography>
                                <FieldArray name={`${path}.items`}>
                                    {({push: pushSub, remove: removeSub, form}) => (
                                        <Table size="small" sx={{border: 1}}>
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell width="10%">STT</TableCell>
                                                    <TableCell>Loại đánh giá</TableCell>
                                                    <TableCell>Loại nội dung</TableCell>
                                                    <TableCell align="center" width="15%">Hành động</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {(item.items || []).map((sub, subIndex) => (
                                                    <RecursiveItem
                                                        key={sub.id || subIndex} // Sử dụng id nếu có
                                                        item={sub}
                                                        index={subIndex}
                                                        parentPath={`${path}.items`}
                                                        setFieldValue={setFieldValue}
                                                        remove={removeSub}
                                                    />
                                                ))}
                                                <TableRow>
                                                    <TableCell colSpan={4}>
                                                        <Button
                                                            variant="text"
                                                            onClick={() => pushSub({
                                                                name: "",
                                                                items: [],
                                                                child: false,
                                                                numberOrder: (item.items || []).length + 1
                                                            })}
                                                            startIcon={<AddIcon/>}
                                                            size="small"
                                                        >
                                                            Thêm mục con
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            </TableBody>
                                        </Table>
                                    )}
                                </FieldArray>
                            </Box>
                        </Collapse>
                    </TableCell>
                </TableRow>
            )}
        </React.Fragment>
    );
};
