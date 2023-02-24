<script>
    import {
        BreadcrumbItem, Button,
        TableBodyCell,
        TableBodyRow
    } from "flowbite-svelte";
    import {createEventDispatcher, onMount} from "svelte";

    export let request;
    export let entry;
    export let pending = false;
    export let accepted = false;
    export let error;

    const dispatch = createEventDispatcher();


    function updateRequest(state) {
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
                    alert("You have "+ state.toLocaleLowerCase() +" the review request")
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
        reviews.forEach( pair => {
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

<TableBodyRow>
    <TableBodyCell>
        {#if accepted && reviews !== null}
            <BreadcrumbItem href="categories/{entry.category_id}/entries/{entry.id}/reviews/{getReviewToPaper(entry)}">{entry.name}</BreadcrumbItem>
        {:else }
            <BreadcrumbItem href="categories/{entry.category_id}/entries/{entry.id}">{entry.name}</BreadcrumbItem>
        {/if}
    </TableBodyCell>
    {#if pending}
        <TableBodyCell>
            <div class="justify-center flex w-full gap-x-2">
                <Button pill class="!p-2" outline
                        on:click|once={() => updateRequest("ACCEPTED")}>
                    Accept
                </Button>

                <Button pill class="!p-2" outline color="red"
                        on:click|once={() => updateRequest("DECLINED")}>
                    Decline
                </Button>

            </div>
        </TableBodyCell>
    {/if}
</TableBodyRow>
