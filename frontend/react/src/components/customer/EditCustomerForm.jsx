import {Form, Formik, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Stack, VStack, Image} from "@chakra-ui/react";
import {customerProfilePictureUrl, editCustomer, uploadCustomerProfilePicture} from "../../services/client.js";
import {successNotification, errorNotification} from "../../services/notification.js";
import {useCallback} from "react";
import {useDropzone} from "react-dropzone";

const MyTextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MyDropzone = ({ id, fetchCustomers }) => {
    const onDrop = useCallback(acceptedFiles => {
        const formData = new FormData();
        formData.append("file", acceptedFiles[0])

        uploadCustomerProfilePicture(
            id,
            formData
        ).then(()=> {
            successNotification("Success", "Profile picture uploaded")
            fetchCustomers()
            }
        ).catch((e) => {
            errorNotification("ERROR", "Profile picture failed upload")
        })
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             borderRadius={'3xl'}
             p={6}
             rounded={'md'}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the picture here ...</p> :
                    <p>Drag 'n' drop some pictures here, or click to select pictures</p>
            }
        </Box>
    )
}

// And now we can use these
export const EditCustomerForm = ({initialValues, fetchCustomers }) => {
    return (
        <>
            <VStack spacing={'5'} mb={'5'}>
                <Image
                    borderRadius={'full'}
                    boxSize={'150px'}
                    objectFit={'cover'}
                    src={customerProfilePictureUrl(initialValues.id)}
                />
                <MyDropzone id={initialValues.id}
                            fetchCustomers={fetchCustomers}
                />
            </VStack>
            <Formik
                initialValues={{
                    name: initialValues.name,
                    email: initialValues.email,
                    age: initialValues.age,
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less'),
                    email: Yup.string()
                        .email('This is not an email'),
                    age: Yup.number()
                        .min(16, 'Must be at least 16 years of age')
                        .max(100, 'Must be less than 100 years of age'),
                })}
                onSubmit={(updatedCustomer, {setSubmitting}) => {
                    setSubmitting(true);
                    editCustomer(initialValues.id, updatedCustomer)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Changes saved",
                                `Changes to ${updatedCustomer.name} were successfully saved`
                            )
                            fetchCustomers();
                        }).catch(err => {
                        console.log(err);
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}
            >
                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                                placeholder="Jane"
                            />

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                                placeholder="jane@formik.com"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="20"
                            />

                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};