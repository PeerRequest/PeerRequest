<script>

    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import Container from "../components/Container.svelte";
    import ResponsiveBreadCrumb from "../components/ResponsiveBreadCrumb.svelte";
    import {onMount} from "svelte";
    import Requests from "../components/Requests.svelte";
    import Request from "../components/Request.svelte";

    export let error;

    let requests = [];
    let pending_requests = [];
    let accepted_requests = [];


    function loadRequests() {
        requests = [];

        fetch("/api/requests")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    requests = resp.content;
                }
            })
            .catch(err => console.log(err))
    }


    function filterRequests() {
        if (requests !== null) {
            pending_requests = requests.filter(request => request.state === "PENDING")
            accepted_requests = requests.filter(request => request.state === "ACCEPTED")
        } else {
            console.log("No requests found.")
        }
    }


    onMount(() => {
        loadRequests()
        filterRequests()
    });
</script>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">Home</Heading>


    <div class="justify-center flex w-full gap-x-4">
        <Requests
                pending=true
        >
            {#each pending_requests as pr}
                <Request
                        request={pr}
                        pending=true
                />
            {/each}
        </Requests>
        <Requests
                accepted=true
        >
            {#each accepted_requests as ar}
                <Request
                        request={ar}
                        accepted=true
                />
            {/each}
        </Requests>
    </div>

</Container>
