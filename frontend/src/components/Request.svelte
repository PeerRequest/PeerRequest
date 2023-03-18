<script>
    import {
        A, Button,
        TableBodyCell,
        TableBodyRow,
        Toast
    } from "flowbite-svelte";
    import {createEventDispatcher, onMount} from "svelte";

    export let request;
    export let entry;
    export let pending = false;
    export let accepted = false;
    export let error;

    let show_request_notification = false;
    let counter = 6;
    let request_state = ""
    let isLoading = false;

    function triggerNotification() {
        isLoading = true;
        show_request_notification = true;
        counter = 6;
        timeout();
    }

    function timeout() {
        if (--counter > 0)
            return setTimeout(timeout, 1000);
        show_request_notification = false;
    }

    const dispatch = createEventDispatcher();


    function updateRequest(state) {
        request_state = state;
        triggerNotification()
        let data = {
            state: state
        };
        fetch("/api/categories/" + entry.category_id + "+/entries/" + entry.id + "/process/requests", {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    dispatch("requestUpdated", state);
                }
            })
            .catch(err => console.log(err))
    }

    let reviews = null;


    function loadUserReviews() {
        reviews = null;
        fetch("/api/reviews")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviews = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    function getReviewToPaper(paper) {
        let review_id = ""
        reviews.forEach(pair => {
            if (paper.id === pair.second.id) {
                review_id = pair.first.id;
            }
        })
        return review_id;
    }

    onMount(() => {
        if (accepted) {
            loadUserReviews();
        }
    })
</script>

{#if request_state === "ACCEPTED"}
    <Toast color="green" class="mb-2 fixed max-w-fit w-[40vw] bottom-0 right-[35vw]"
           bind:open={show_request_notification}>
        <svelte:fragment slot="icon">
            <svg aria-hidden="true"
                 class="w-5 h-5"
                 fill="currentColor"
                 viewBox="0 0 20 20"
                 xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd"
                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                      clip-rule="evenodd"></path>
            </svg>
            <span class="sr-only">Check icon</span>
        </svelte:fragment>
        You have accepted the review request
    </Toast>
{:else }
    <Toast color="red" class="mb-2 fixed max-w-fit w-[40vw] bottom-0 right-[35vw]"
           bind:open={show_request_notification}>
        <svelte:fragment slot="icon">
            <svg aria-hidden="true" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                 xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd"
                      d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                      clip-rule="evenodd"></path>
            </svg>
            <span class="sr-only">Error icon</span>
        </svelte:fragment>
        You have declined the review request
    </Toast>
{/if}
<TableBodyRow>
    <TableBodyCell>
        {#if accepted && reviews !== null}
            <A aria-label="accepted_request" href="categories/{entry.category_id}/entries/{entry.id}/reviews/{getReviewToPaper(entry)}">{entry.name}</A>
        {:else }
            <A aria-label="pending_request" href="categories/{entry.category_id}/entries/{entry.id}">{entry.name}</A>
        {/if}
    </TableBodyCell>
    {#if pending}
        <TableBodyCell>
            <div class="justify-center flex w-full gap-x-2">
                {#if isLoading}
                    Loading
                {:else }
                    <Button aria-label="accept-request" pill class="!p-2" outline
                            on:click|once={() => updateRequest("ACCEPTED")}>
                        Accept
                    </Button>

                    <Button aria-label="decline-request" pill class="!p-2" outline color="red"
                            on:click|once={() => updateRequest("DECLINED")}>
                        Decline
                    </Button>
                {/if}
            </div>
        </TableBodyCell>
    {/if}
</TableBodyRow>
