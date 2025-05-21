import {
    Divider,
    FormControl,
    FormControlLabel,
    Grid,
    Paper,
    Radio,
    RadioGroup,
    Tooltip,
    Typography,
} from "@material-ui/core";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";

const EvaluationValuesForm = () => {
    const { values, setFieldValue } = useFormikContext();
    return (
        <>
            <Paper elevation={2} style={{ padding: "16px" }}>
                <Typography variant='h6' style={{ marginBottom: 10 }}>
                    TIÊU CHÍ TUYỂN CHỌN
                </Typography>

                <Divider style={{ margin: "0" }} />

                {values?.evaluationValues?.map((item) => (
                    <TreeNode key={item.id} node={item} level={0} />
                ))}
            </Paper>
            <InterviewResultRadioGroup />
        </>
    );
};

const interviewResults = [
    { value: "1", label: "Đạt yêu cầu, chuyển làm thủ tục nhận việc" },
    { value: "2", label: "Đạt yêu cầu, dự phòng" },
    { value: "3", label: "Xem xét vị trí khác" },
    { value: "4", label: "Không đạt yêu cầu" },
];
// Component riêng cho radio group kết quả phỏng vấn với layout 2x2
const InterviewResultRadioGroup = observer(() => {
    const { values, setFieldValue } = useFormikContext();

    const handleResultChange = (e) => {
        setFieldValue("result", e.target.value); // dùng string
    };

    const handleRadioClick = (e, optionValue) => {
        e.preventDefault();
        const newValue = values.result === optionValue ? null : optionValue;
        setFieldValue("result", newValue);
    };

    return (
        <Paper elevation={2} style={{ padding: "16px" }}>
            <Typography variant='subtitle2' style={{ margin: "16px 0", fontWeight: "bold" }}>
                Chú thích:
                <span style={{ fontWeight: "normal", marginLeft: 8 }}>
                    1. Không hài lòng &nbsp;&nbsp; 2. Tạm được &nbsp;&nbsp; 3. Hài lòng &nbsp;&nbsp; 4. Tốt &nbsp;&nbsp;
                    5. Rất tốt
                </span>
            </Typography>
            <Typography variant='h6' style={{ marginBottom: 10 }}>
                KẾT QUẢ
            </Typography>

            <Divider style={{ paddingBotton: "16px" }} />
            <Grid item xs={12} style={{ paddingTop: "16px" }}>
                <FormControl component='fieldset' fullWidth>
                    <RadioGroup
                        aria-label='result'
                        name='result'
                        style={{ margin: 0 }}
                        value={values.result?.toString() || ""}
                        onChange={handleResultChange}>
                        <Grid container spacing={2}>
                            {interviewResults.map((option) => (
                                <Grid item xs={6} key={option.value}>
                                    <FormControlLabel
                                        value={option.value}
                                        control={
                                            <Radio
                                                color='primary'
                                                style={{ margin: 0 }}
                                                onClick={(e) => handleRadioClick(e, option?.value)}
                                            />
                                        }
                                        label={option.label}
                                    />
                                </Grid>
                            ))}
                        </Grid>
                    </RadioGroup>
                </FormControl>
            </Grid>
        </Paper>
    );
});

function TreeNode({ node, level }) {
    const { setFieldValue, values } = useFormikContext();
    const hasChildren = node?.children && node?.children.length > 0;
    const isContentNode = node.item?.contentType === "CONTENT";
    const isTitleNode = node.item?.contentType === "TITLE";

    const displayName = node.item?.item?.name || node?.name || "Unnamed Item";
    const paddingLeft = `${level * 16}px`;

    // Lấy giá trị từ node.value nếu có, nếu không thì từ Formik values
    const fieldValue = node.value !== null ? node.value.toString() : values.values?.[node.id]?.toString() || "";

    const handleRadioClick = (num) => (e) => {
        // Ngăn chặn hành vi mặc định để tự kiểm soát sự kiện
        e.preventDefault();

        const clickedValue = num.toString();
        const newValue = fieldValue === clickedValue ? null : clickedValue;

        node.value = newValue !== null ? parseInt(newValue) : null;
        const filtered = values?.evaluations?.filter((item) => item.evaluationValueId !== node.id);
        const newEvaluations = [
            ...filtered,
            ...(newValue !== null
                ? [
                      {
                          evaluationValueId: node.id,
                          value: parseInt(newValue),
                      },
                  ]
                : []),
        ];
        setFieldValue(`evaluations`, newEvaluations);
    };

    // Định nghĩa tooltip cho từng mức đánh giá
    const ratingLabels = {
        1: "Không hài lòng",
        2: "Tạm được",
        3: "Hài lòng",
        4: "Tốt",
        5: "Rất tốt",
    };

    return (
        <div className='tree-node' style={{ width: "100%" }}>
            <div
                className={`flex items-center py-2 ${level > 0 ? "border-t border-gray-100" : ""}`}
                style={{ paddingLeft }}>
                <div className='flex-1 flex items-center justify-between'>
                    <span className={`${isTitleNode ? "font-semibold py-4" : ""}`}>{displayName}</span>

                    {isContentNode && (
                        <FormControl component='fieldset' style={{ marginLeft: "16px" }}>
                            <RadioGroup
                                row
                                aria-label='rating'
                                name={`rating-${node.id}`}
                                value={fieldValue}
                            >
                                {[1, 2, 3, 4, 5].map((num) => (
                                    <Tooltip key={num} title={ratingLabels[num]} placement='top' arrow>
                                        <FormControlLabel
                                            value={num.toString()}
                                            control={
                                                <Radio
                                                    size='small'
                                                    style={{
                                                        margin: 0,
                                                        padding: "6px",
                                                        transition: "all 0.3s",
                                                        "&:hover": {
                                                            transform: "scale(1.2)",
                                                        },
                                                    }}
                                                    onClick={handleRadioClick(num)}
                                                />
                                            }
                                            label={num.toString()}
                                            labelPlacement='bottom'
                                            style={{
                                                margin: "0 4px",
                                                borderRadius: "50%",
                                                transition: "all 0.3s",
                                                "&:hover": {
                                                    backgroundColor: "rgba(0, 0, 0, 0.04)",
                                                },
                                            }}
                                        />
                                    </Tooltip>
                                ))}
                            </RadioGroup>
                        </FormControl>
                    )}
                </div>
            </div>

            {/* Luôn hiển thị children nếu có */}
            {hasChildren && (
                <div className='children'>
                    {node.children.map((child) => (
                        <TreeNode
                            key={child.id}
                            node={child}
                            level={level + 1}
                            setFieldValue={setFieldValue}
                            values={values}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

export default observer(EvaluationValuesForm);
